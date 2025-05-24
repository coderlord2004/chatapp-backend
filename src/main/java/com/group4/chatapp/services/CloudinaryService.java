package com.group4.chatapp.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final AttachmentService attachmentService;
    // upload avatar
    public String uploadFile(MultipartFile file) {
        try {
            Map<String, Object> options = Map.of(
                    "folder", "Image/"
            );
            return (String) cloudinary.uploader().upload(file.getBytes(), options).get("secure_url");
        } catch (Exception e) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }

    public List<Map> uploadMutiFile(List<MultipartFile> files) throws InterruptedException {
        if (files == null || files.isEmpty()) {
            return null;
        }

        ExecutorService executor = Executors.newFixedThreadPool(files.size());
        List<Future<Map>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            String resourceType = attachmentService.getMimeType(file.getContentType());
            futures.add(executor.submit(() -> {
                try {
                    Map result = cloudinary.uploader().upload(file.getBytes(), Map.of(
                            "folder", resourceType,
                            "resource_type", resourceType
                    ));
                    System.out.println("Uploaded: " + file.getOriginalFilename());
                    return result;
                } catch (Exception e) {
                    throw new ApiException(
                            HttpStatus.BAD_REQUEST,
                            e.getMessage()
                    );
                }
            }));
        }

        // Thu kết quả upload
        List<Map> uploadResults = new ArrayList<>();
        int index = 0;
        for (Future<Map> future : futures) {
            MultipartFile currentFile = files.get(index);
            try {
                Map result = future.get();

                if (result != null) {
                    uploadResults.add(Map.of(
                            "filename", Objects.requireNonNull(currentFile.getOriginalFilename()),
                            "status", "success",
                            "secure_url", result.get("secure_url"),
                            "resource_type", result.get("resource_type"),
                            "format", attachmentService.getFileExtension(currentFile.getOriginalFilename())
                        )
                    );
                }
            } catch (ExecutionException | InterruptedException e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("filename", currentFile.getOriginalFilename());
                errorResult.put("status", "error");
                errorResult.put("message", e.getCause().getMessage());
                uploadResults.add(errorResult);
            }
            index++;
        }

        executor.shutdown(); // đóng Executor
        executor.awaitTermination(1, TimeUnit.MINUTES); // đợi các task xong

        return uploadResults;
    }

}
