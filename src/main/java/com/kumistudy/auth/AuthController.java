package com.kumistudy.auth;

import com.kumistudy.auth.dto.LoginRequest;
import com.kumistudy.auth.dto.RegisterRequest;
import com.kumistudy.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, String>>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "註冊成功",
                "username", request.username()
        )));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @Valid @RequestBody LoginRequest request,
                        HttpServletRequest servletRequest,
                        HttpServletResponse servletResponse) {
                User user = authService.login(request, servletRequest, servletResponse);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "登入成功",
                "username", user.getUsername(),
                "displayName", user.getDisplayName()
        )));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, String>>> currentUser(
            Authentication authentication,
            HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        String displayName = session == null
                ? authentication.getName()
                : (String) session.getAttribute("loginDisplayName");

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "username", authentication.getName(),
                "displayName", displayName == null ? authentication.getName() : displayName
        )));
    }

    @GetMapping("/csrf")
    public ApiResponse<Map<String, String>> csrf(CsrfToken csrfToken) {
        return ApiResponse.success(Map.of(
                "headerName", csrfToken.getHeaderName(),
                "token", csrfToken.getToken()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest servletRequest) {
        authService.logout(servletRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
