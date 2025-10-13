package com.group4.chatapp.services;

import com.cloudinary.Cloudinary;
import com.group4.chatapp.dtos.UpdateFileDto;
import com.group4.chatapp.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final FileTypeService fileTypeService;

    public String getPublicIdByUrl(String url) {
        if (url == null || url.isEmpty()) return null;

        int index = url.lastIndexOf("image/");
        if (index == -1) return null;

        return url.substring(index, url.lastIndexOf('.'));
    }

    public String uploadFile(MultipartFile file) {
        try {
            String folderName = fileTypeService.getMimeType(file.getContentType());
            var options = Map.of("folder", folderName);

            return (String) cloudinary.uploader()
                .upload(file.getBytes(), options)
                .get("secure_url");

        } catch (Exception e) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
            );
        }
    }

    public void deleteFile(String url) {
        try {
            String publicId = getPublicIdByUrl(url);
            cloudinary.uploader().destroy(publicId, new HashMap<>());

        } catch (Exception e) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }

    private List<Future<Map<?, ?>>> getFutures(
        ExecutorService executor,
        List<MultipartFile> files
    ) {

        var futures = new ArrayList<Future<Map<?, ?>>>();

        for (var file : files) {
            System.out.println("in get future: " + file.getOriginalFilename());
            String resourceType = fileTypeService.getMimeType(file.getContentType());
            String cloudinaryResourceType = fileTypeService.getCloudinaryResourceType(file.getContentType());

            futures.add(executor.submit(() -> {
                try {

                    var result = cloudinary.uploader().upload(file.getBytes(), Map.of(
                        "folder", resourceType,
                        "resource_type", cloudinaryResourceType
                    ));

                    System.out.println("Uploaded: " + file.getOriginalFilename());
                    return (Map<?, ?>) result;

                } catch (Exception e) {
                    throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        e.getMessage()
                    );
                }
            }));
        }

        return futures;
    }

    private List<Map<String, ?>> collectUploadResults(List<MultipartFile> files, List<Future<Map<?, ?>>> futures) {

        var uploadResults = new ArrayList<Map<String, ?>>();

        for (int i = 0; i < futures.size(); i++) {

            var currentFile = files.get(i);
            var filename = fileTypeService.getName(Objects.requireNonNull(currentFile.getOriginalFilename()));

            Map<String, ?> uploadResult;

            try {

                var result = futures.get(i).get();
                if (result == null) {
                    continue;
                }

                uploadResult = Map.of(
                    "filename", filename,
                    "status", "success",
                    "secure_url", result.get("secure_url"),
                    "resource_type", result.get("resource_type"),
                    "format", fileTypeService.getFileExtension(currentFile.getOriginalFilename())
                );
            } catch (ExecutionException | InterruptedException e) {
                uploadResult = Map.of(
                    "filename", filename,
                    "status", "error",
                    "message", e.getCause().getMessage()
                );
            }

            uploadResults.add(uploadResult);
        }

        return uploadResults;
    }

    @Nullable
    public List<Map<String, ?>> uploadMultiFile(@Nullable List<MultipartFile> files) {
        try {
            if (CollectionUtils.isEmpty(files)) {
                return null;
            }

            var executor = Executors.newFixedThreadPool(files.size());

            var futures = getFutures(executor, files);
            var uploadResults = collectUploadResults(files, futures);

            executor.shutdown();
            var isTimeout = executor.awaitTermination(1, TimeUnit.MINUTES);

            if (isTimeout) {
                // TODO handle this
            }

            return uploadResults;
        } catch (Exception e) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }

    public void deleteMultiFile(List<String> publicIds) {
        publicIds.forEach(publicId -> {
            try {
                cloudinary.uploader().destroy(publicId, Map.of("invalidate", true));
            } catch (IOException e) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        e.getMessage()
                );
            }
        });
    }
}
