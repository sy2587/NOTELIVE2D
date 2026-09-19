package com.kumistudy.auth;

import org.springframework.web.bind.annotation.PostMapping;
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
    public void register() {
        // TODO: 改成接收 @Valid RegisterRequest，呼叫 service 並回傳統一 ApiResponse。
    }

    @PostMapping("/login")
    public void login() {
        // TODO: 接收登入 DTO，成功後建立 Session，不要把密碼放進回應。
    }

    @PostMapping("/logout")
    public void logout() {
        // TODO: 注入 HttpServletRequest/HttpSession 後執行登出並回傳 204 或成功回應。
    }
}
