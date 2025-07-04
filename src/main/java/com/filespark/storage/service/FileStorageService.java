package com.filespark.storage.service;

import com.filespark.storage.model.request.FileUploadRequest;
import com.filespark.storage.model.response.FileInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    FileInfoResponse upload(String userId, MultipartFile file, FileUploadRequest metadata);

    FileInfoResponse rename(String userId, String fileId, String newName);

    Page<FileInfoResponse> listPublicFiles(String tag, Pageable pageable);

    Page<FileInfoResponse> listUserFiles(String userId, String tag, Pageable pageable);

    ResponseEntity<?> download(String link);

    void delete(String userId, String fileId);
}
