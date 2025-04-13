package com.group4.chatapp.dtos;

import com.group4.chatapp.models.File;

public record FileDto(
    String source,
    File.FileType type
) {

    public FileDto(File file) {
        this(file.getSource(), file.getType());
    }
}
