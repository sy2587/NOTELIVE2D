package com.kumistudy.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "待辦事項不可空白")
        @Size(max = 255, message = "待辦事項不可超過 255 個字")
        String title
) {
}
