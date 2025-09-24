package com.group4.chatapp.services;

import com.group4.chatapp.dtos.UploadFileDto;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.Attachment.FileType;
import com.group4.chatapp.repositories.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final FileTypeService fileTypeService;
    private final CloudinaryService cloudinaryService;
    private final AttachmentRepository attachmentRepository;

    public List<Attachment> saveFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, ?>> uploadedFiles = cloudinaryService.uploadMultiFile(files);
        if (uploadedFiles == null || uploadedFiles.isEmpty()) {
            return Collections.emptyList();
        }

        return uploadedFiles.stream()
                .filter(file -> "success".equals(file.get("status")))
                .map(this::mapToAttachment)
                .map(attachmentRepository::save)
                .collect(Collectors.toList());
    }

    public List<Attachment> saveFilesWithDescription(List<UploadFileDto> uploadFileDtos) {
        if (uploadFileDtos == null || uploadFileDtos.isEmpty()) {
            return Collections.emptyList();
        }

        List<MultipartFile> files = uploadFileDtos.stream()
                .map(UploadFileDto::getFile)
                .filter(Objects::nonNull)
                .toList();

        List<Map<String, ?>> uploadedFiles = cloudinaryService.uploadMultiFile(files);
        if (uploadedFiles == null || uploadedFiles.isEmpty()) {
            return Collections.emptyList();
        }

        List<Attachment> attachments = new ArrayList<>();
        for (int i = 0; i < uploadedFiles.size(); i++) {
            Map<String, ?> file = uploadedFiles.get(i);

            if (!"success".equals(file.get("status"))) {
                continue;
            }

            String description = uploadFileDtos.get(i).getDescription();
            Attachment attachment = mapToAttachment(file, description);

            attachments.add(attachment);
        }

        return attachments;
    }

    private Attachment mapToAttachment(Map<String, ?> file) {
        return mapToAttachment(file, null);
    }

    private Attachment mapToAttachment(Map<String, ?> file, String description) {
        String fileName = (String) file.get("filename");
        String source = (String) file.get("secure_url");
        String resourceType = (String) file.get("resource_type");
        String format = (String) file.get("format");

        FileType type = fileTypeService.checkTypeInFileType(resourceType, format);

        return Attachment.builder()
                .name(fileName)
                .source(source)
                .type(type)
                .format(format)
                .description(description)
                .build();
    }
}
