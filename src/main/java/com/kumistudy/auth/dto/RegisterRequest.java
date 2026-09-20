package com.kumistudy.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "使用者名稱不能為空")
        @Size(min = 3, max = 50, message = "使用者名稱長度需介於 3 到 50 字元")
        String username,

        @NotBlank(message = "電子信箱不能為空")
        @Email(message = "電子信箱格式不正確")
        @Size(max = 255, message = "電子信箱過長")
        String email,

        @NotBlank(message = "密碼不能為空")
        @Size(min = 8, max = 255, message = "密碼長度需至少 8 字元")
        String password,

        @NotBlank(message = "顯示名稱不能為空")
        @Size(min = 2, max = 100, message = "顯示名稱長度需介於 2 到 100 字元")
        String displayName
) {
}
