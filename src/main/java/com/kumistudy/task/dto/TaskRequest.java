package com.kumistudy.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "待辦事項不可空白")
        @Size(max = 255, message = "待辦事項不可超過 255 個字")
        String title,
        @Size(max = 2000, message = "待辦說明不可超過 2000 個字")
        String description,
        @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "優先級必須是 LOW、MEDIUM 或 HIGH")
        String priority,
        LocalDate dueDate
) {
    public TaskRequest(String title) { this(title, null, null, null); }
}
