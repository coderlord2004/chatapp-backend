package com.group4.chatapp.services;

import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.repositories.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final FileTypeService fileTypeService;
    private final CloudinaryService cloudinaryService;

    private final AttachmentRepository attachmentRepository;

    public List<Attachment> getAttachments(MessageSendDto dto) {

        List<Map<String, ?>> uploadedFiles;
        try {
            var attachments = dto.getAttachments();
            uploadedFiles = cloudinaryService.uploadMutiFile(attachments);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (uploadedFiles == null) {
            return List.of();
        }

        return uploadedFiles
            .stream()
            .map((file) -> {

                var isSuccess = file.get("status").equals("success");
                if (!isSuccess) {
                    return null;
                }

                var fileName = (String) file.get("filename");
                var source = (String) file.get("secure_url");

                var resourceType = (String) file.get("resource_type");
                var format = (String) file.get("format");
                var type = fileTypeService.checkTypeInFileType(resourceType, format);

                var attachment = Attachment.builder()
                        .name(fileName)
                        .source(source)
                        .type(type)
                        .format(format)
                        .build();

                return attachmentRepository.save(attachment);

            })
            .toList();
    }
}
