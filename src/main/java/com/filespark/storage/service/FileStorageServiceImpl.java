package com.filespark.storage.service;

import com.filespark.storage.domain.FileDocument;
import com.filespark.storage.domain.enums.Visibility;
import com.filespark.storage.infra.FileRepository;
import com.filespark.storage.infra.MinioClientConfig;
import com.filespark.storage.infra.exception.DuplicateFileException;
import com.filespark.storage.infra.exception.FileNotOwnedException;
import com.filespark.storage.infra.exception.FileNotFoundException;
import com.filespark.storage.model.response.FileInfoResponse;
import com.filespark.storage.model.request.FileUploadRequest;
import com.filespark.storage.util.FileTypeDetector;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileRepository repository;
    private final MinioClient minioClient;
    private final String minioBucketName;

    public FileStorageServiceImpl(FileRepository repository,
                                  MinioClientConfig minioConfig) {
        this.repository = repository;
        this.minioClient = minioConfig.minioClient();
        this.minioBucketName = minioConfig.getBucketName();
    }

    @Override
    public FileInfoResponse upload(String userId, MultipartFile file, FileUploadRequest metadata) {
        try {
            String filename = metadata.getFilename();
            Visibility visibility = metadata.getVisibility();
            List<String> tags = metadata.getTags();

            if (repository.existsByUserIdAndFilename(userId, filename)) {
                throw new DuplicateFileException("File with same name already exists for this user");
            }

            byte[] fileBytes = file.getBytes();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(fileBytes);
            String contentHash = bytesToHex(digest.digest());

            if (repository.existsByUserIdAndContentHash(userId, contentHash)) {
                throw new DuplicateFileException("File with same content already exists for this user");
            }

            String link = UUID.randomUUID().toString();
            String contentType = FileTypeDetector.detect(file);

            try (InputStream uploadStream = new java.io.ByteArrayInputStream(fileBytes)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioBucketName)
                                .object(link)
                                .stream(uploadStream, fileBytes.length, -1)
                                .contentType(contentType)
                                .build()
                );
            }

            FileDocument doc = new FileDocument();
            doc.setUserId(userId);
            doc.setFilename(filename);
            doc.setVisibility(visibility);
            doc.setTags(tags);
            doc.setLink(link);
            doc.setUploadedAt(Instant.now());
            doc.setSize(file.getSize());
            doc.setContentType(contentType);
            doc.setContentHash(contentHash);

            repository.save(doc);

            return FileInfoResponse.from(doc);
        } catch (DuplicateFileException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public FileInfoResponse rename(String userId, String fileId, String newName) {
        FileDocument doc = repository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        if (!doc.getUserId().equals(userId)) {
            throw new FileNotOwnedException("You don't own this file");
        }

        doc.setFilename(newName);
        repository.save(doc);
        return FileInfoResponse.from(doc);
    }

    @Override
    public Page<FileInfoResponse> listPublicFiles(String tag, Pageable pageable) {
        if (tag != null) {
            return repository.findByVisibilityAndTag(Visibility.PUBLIC, tag, pageable)
                    .map(FileInfoResponse::from);
        }
        return repository.findByVisibility(Visibility.PUBLIC, pageable)
                .map(FileInfoResponse::from);
    }

    @Override
    public Page<FileInfoResponse> listUserFiles(String userId, String tag, Pageable pageable) {
        if (tag != null) {
            return repository.findByUserIdAndTag(userId, tag, pageable)
                    .map(FileInfoResponse::from);
        }
        return repository.findByUserId(userId, pageable)
                .map(FileInfoResponse::from);
    }

    @Override
    public ResponseEntity<?> download(String link) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(link)
                            .build()
            );

            FileDocument fileDoc = repository.findByLink(link)
                    .orElseThrow(() -> new FileNotFoundException("File not found"));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDoc.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(fileDoc.getContentType()))
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            throw new RuntimeException("Download failed", e);
        }
    }

    @Override
    public void delete(String userId, String fileId) {
        FileDocument doc = repository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        if (!doc.getUserId().equals(userId)) {
            throw new FileNotOwnedException("You don't own this file");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(doc.getLink())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from storage");
        }

        repository.deleteById(fileId);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
