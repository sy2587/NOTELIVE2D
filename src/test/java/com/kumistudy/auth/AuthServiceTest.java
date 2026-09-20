package com.kumistudy.auth;

import com.kumistudy.auth.dto.RegisterRequest;
import com.kumistudy.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldHashPasswordAndSaveUser() {
        RegisterRequest request = new RegisterRequest("alice", "alice@example.com", "secret123", "Alice");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("alice", savedUser.getUsername());
        assertEquals("alice@example.com", savedUser.getEmail());
        assertEquals("Alice", savedUser.getDisplayName());
        assertEquals("hashed-secret", savedUser.getPasswordHash());
    }

    @Test
    void register_shouldRejectDuplicateUsername() {
        RegisterRequest request = new RegisterRequest("alice", "alice@example.com", "secret123", "Alice");

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("使用者名稱已存在", exception.getMessage());
    }

    @Test
    void login_shouldCreateSessionForActiveUser() {
        User user = new User("alice", "alice@example.com", "hashed-secret", "Alice");
        LoginRequest request = new LoginRequest("alice", "secret123");
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        when(userRepository.findByUsernameAndDeletedAtIsNull("alice"))
                .thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed-secret")).thenReturn(true);

        authService.login(request, servletRequest, servletResponse);

        HttpSession session = servletRequest.getSession(false);
        assertEquals("alice", session.getAttribute("loginUsername"));
        assertEquals("Alice", session.getAttribute("loginDisplayName"));
    }

    @Test
    void login_shouldRejectWrongPassword() {
        User user = new User("alice", "alice@example.com", "hashed-secret", "Alice");
        LoginRequest request = new LoginRequest("alice", "wrong-password");
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        when(userRepository.findByUsernameAndDeletedAtIsNull("alice"))
                .thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-secret")).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.security.authentication.BadCredentialsException.class,
                () -> authService.login(request, servletRequest, servletResponse)
        );
    }
}
