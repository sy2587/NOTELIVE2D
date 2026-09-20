package com.kumistudy.subject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
        @NotBlank(message = "科目名稱不能為空")
        @Size(max = 150, message = "科目名稱不能超過 150 個字元")
        String name,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "顏色必須是六位十六進位色碼")
        String color,

        @Size(max = 50, message = "圖示名稱不能超過 50 個字元")
        String icon,

        @Size(max = 100, message = "學習目標不能超過 100 個字元")
        String studyGoal,

        @jakarta.validation.constraints.Min(value = 0, message = "進度不能小於 0")
        @jakarta.validation.constraints.Max(value = 100, message = "進度不能超過 100")
        Integer progress,

        @Size(max = 255, message = "備註不能超過 255 個字元")
        String description
) {
}
