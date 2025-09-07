package com.group4.chatapp.services;

import com.group4.chatapp.models.Attachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileTypeService {
    public boolean isImage(MultipartFile file) {
        String contentType = getMimeType(file.getContentType());
        return contentType.equalsIgnoreCase("image");
    }

    public String getFileExtension(String fileName) {
        log.debug("ext: {}", fileName.substring(fileName.lastIndexOf(".") + 1));
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public String getMimeType(String contentType) {

        if (contentType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing content type");
        }

        if (contentType.startsWith("image/")) {
            return "image";
        }

        if (contentType.startsWith("video/")) {
            return "video";
        }

        if (contentType.startsWith("audio/")) {
            return "audio";
        }

        return "raw";
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

                if (Attachment.isDocumentFormat(format)) {
                    return Attachment.FileType.DOCUMENT;
                }

                if (Attachment.isAudioFormat(format)) {
                    return Attachment.FileType.AUDIO;
                }

                return Attachment.FileType.RAW;

            default:
                throw new com.group4.chatapp.exceptions.ApiException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "File type is not supported!"
                );
        }
    }
}
