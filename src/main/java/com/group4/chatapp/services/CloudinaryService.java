package com.group4.chatapp.services;

import com.cloudinary.Cloudinary;
import com.group4.chatapp.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final FileTypeService fileTypeService;

    // upload avatar
    public String uploadFile(MultipartFile file) {
        try {

            var options = Map.of("folder", "Image/");

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

    private List<Future<Map<?, ?>>> getFutures(
        ExecutorService executor,
        List<MultipartFile> files
    ) {

        var futures = new ArrayList<Future<Map<?, ?>>>();

        for (var file : files) {

            var resourceType = fileTypeService.getMimeType(file.getContentType());

            futures.add(executor.submit(() -> {
                try {

                    var result = cloudinary.uploader().upload(file.getBytes(), Map.of(
                        "folder", resourceType,
                        "resource_type", resourceType
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
            var filename = Objects.requireNonNull(currentFile.getOriginalFilename());

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
    public List<Map<String, ?>> uploadMutiFile(@Nullable List<MultipartFile> files) throws InterruptedException {

        if (CollectionUtils.isEmpty(files)) {
            return null;
        }

        var executor = Executors.newFixedThreadPool(files.size());

        var futures = getFutures(executor, files);
        var uploadResults = collectUploadResults(files, futures);

        executor.shutdown();
        var isTimeout = executor.awaitTermination(1, TimeUnit.MINUTES);

        // noinspection StatementWithEmptyBody
        if (isTimeout) {
            // TODO handle this
        }

        return uploadResults;
    }
}
