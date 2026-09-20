package com.kumistudy.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "使用者名稱不能為空")
        String username,

        @NotBlank(message = "密碼不能為空")
        String password
) {
}
