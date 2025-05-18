package com.group4.chatapp.services;

import com.group4.chatapp.models.Attachment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final Attachment attachment = new Attachment();

    public String getFileExtension(String fileName) {
        System.out.println("ext: " + fileName.substring(fileName.lastIndexOf(".") + 1));
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public String getMimeType(String contentType) {
        if (contentType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing content type");
        }
        if (contentType.startsWith("image/")) return "image";
        if (contentType.startsWith("video/")) return "video";
        else return "raw";
    }

    public Attachment.FileType checkTypeInFileType(String resourceType, String format) {
        if (resourceType == null || format == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing file metadata");
        }
        switch (resourceType.toLowerCase()) {
            case "image":
                return Attachment.FileType.IMAGE;

            case "video":
                return Attachment.FileType.VIDEO;

            case "raw":
                // Nếu là tài liệu phổ biến thì xét là DOCUMENT
                if (attachment.isDocumentFormat(format)) {
                    return Attachment.FileType.DOCUMENT;
                }

                // Nếu là file âm thanh
                if (attachment.isAudioFormat(format)) {
                    return Attachment.FileType.AUDIO;
                }

                return Attachment.FileType.RAW;

            default:
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File type is not supported!");
        }
    }
}
