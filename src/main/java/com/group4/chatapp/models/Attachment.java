package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    private static Set<String> DOCUMENT_FORMATS = Set.of(
        "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt"
    );

    private static Set<String> AUDIO_FORMATS = Set.of(
        "mp3", "wav", "aac", "flac", "ogg"
    );

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Enumerated(EnumType.ORDINAL)
    private FileType type;

    public static boolean isDocumentFormat(String format) {
        return DOCUMENT_FORMATS.contains(format.toLowerCase());
    }

    public static boolean isAudioFormat(String format) {
        return AUDIO_FORMATS.contains(format.toLowerCase());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isImage() {
        return type == FileType.IMAGE;
    }

    public enum FileType {
        IMAGE,
        VIDEO,
        RAW,
        DOCUMENT,
        AUDIO
    }
}
