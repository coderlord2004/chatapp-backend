package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Enumerated(EnumType.ORDINAL)
    private FileType type;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isImage() {
        return type == FileType.IMAGE;
    }

    public boolean isDocumentFormat(String format) {
        return format != null && List.of("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt")
                .contains(format.toLowerCase());
    }

    public boolean isAudioFormat(String format) {
        return format != null && List.of("mp3", "wav", "aac", "flac", "ogg")
                .contains(format.toLowerCase());
    }

    public enum FileType {
        IMAGE,
        VIDEO,
        RAW,
        DOCUMENT,
        AUDIO
    }
}
