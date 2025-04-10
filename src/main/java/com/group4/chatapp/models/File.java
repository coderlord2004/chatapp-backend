package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

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

    enum FileType {
        IMAGE, OTHERS
    }
}
