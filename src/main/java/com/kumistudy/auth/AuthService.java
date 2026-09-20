package com.kumistudy.auth;

import com.kumistudy.auth.dto.LoginRequest;
import com.kumistudy.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("使用者名稱已存在");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("電子信箱已存在");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(
                request.username(),
                request.email(),
                encodedPassword,
                request.displayName()
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(request.username())
                .orElseThrow(() -> new BadCredentialsException("帳號或密碼錯誤"));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalStateException("帳號已停用");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("帳號或密碼錯誤");
        }

        HttpSession session = servletRequest.getSession(true);
        session.setAttribute("loginUserId", user.getId());
        session.setAttribute("loginUsername", user.getUsername());
        session.setAttribute("loginDisplayName", user.getDisplayName());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            null,
            java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        new HttpSessionSecurityContextRepository().saveContext(securityContext, servletRequest, servletResponse);

        return user;
    }

    public void logout(HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}
