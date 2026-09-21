package com.kumistudy.folder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FolderRequest(
        @NotBlank(message = "資料夾名稱不可空白")
        @Size(max = 100, message = "資料夾名稱不可超過 100 個字")
        String name
) { }
