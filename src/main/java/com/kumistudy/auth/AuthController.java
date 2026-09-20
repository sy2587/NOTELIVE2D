package com.kumistudy.auth;

import com.kumistudy.auth.dto.LoginRequest;
import com.kumistudy.auth.dto.RegisterRequest;
import com.kumistudy.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest servletRequest) {
        authService.logout(servletRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
