package com.kumistudy.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotBlank(message = "標籤名稱不可空白")
        @Size(max = 50, message = "標籤名稱不可超過 50 個字")
        String name
) { }
