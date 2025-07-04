package com.filespark.storage.service;

import com.filespark.storage.domain.FileDocument;
import com.filespark.storage.domain.enums.FileType;
import com.filespark.storage.domain.enums.Visibility;
import com.filespark.storage.infra.FileRepository;
import com.filespark.storage.infra.MinioClientConfig;
import com.filespark.storage.infra.exception.DuplicateFileException;
import com.filespark.storage.infra.exception.FileNotOwnedException;
import com.filespark.storage.model.request.FileUploadRequest;
import com.filespark.storage.model.response.FileInfoResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    private FileRepository repository;
    private MinioClient minioClient;
    private FileStorageServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(FileRepository.class);
        MinioClientConfig config = mock(MinioClientConfig.class);
        minioClient = mock(MinioClient.class);
        when(config.minioClient()).thenReturn(minioClient);
        when(config.getBucketName()).thenReturn("test-bucket");

        service = new FileStorageServiceImpl(repository, config);
    }



    @Test
    void shouldUploadFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        FileUploadRequest metadata = new FileUploadRequest("test.txt", FileType.DOCUMENT, Visibility.PUBLIC, List.of("tag1"));

        when(repository.existsByUserIdAndFilename(any(), any())).thenReturn(false);
        when(repository.existsByUserIdAndContentHash(any(), any())).thenReturn(false);

        FileInfoResponse response = service.upload("user1", file, metadata);

        assertEquals("test.txt", response.getFilename());
    }

    @Test
    void shouldPreventDuplicateByFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        FileUploadRequest metadata = new FileUploadRequest("test.txt", FileType.DOCUMENT, Visibility.PUBLIC, List.of("tag1"));

        when(repository.existsByUserIdAndFilename(any(), any())).thenReturn(true);

        assertThrows(DuplicateFileException.class, () -> service.upload("user1", file, metadata));
    }

    @Test
    void shouldPreventDuplicateByContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "other.txt", "text/plain", "content".getBytes());
        FileUploadRequest metadata = new FileUploadRequest("other.txt", FileType.DOCUMENT, Visibility.PUBLIC, List.of("tag1"));

        when(repository.existsByUserIdAndFilename(any(), any())).thenReturn(false);
        when(repository.existsByUserIdAndContentHash(any(), any())).thenReturn(true);

        assertThrows(DuplicateFileException.class, () -> service.upload("user1", file, metadata));
    }

    @Test
    void shouldRenameFile() {
        FileDocument fileDoc = new FileDocument();
        fileDoc.setUserId("user1");
        fileDoc.setFilename("old.txt");

        when(repository.findById("file123")).thenReturn(Optional.of(fileDoc));

        FileInfoResponse result = service.rename("user1", "file123", "new.txt");

        assertEquals("new.txt", result.getFilename());
    }

    @Test
    void shouldRejectRenameByOtherUser() {
        FileDocument fileDoc = new FileDocument();
        fileDoc.setUserId("otherUser");
        fileDoc.setFilename("old.txt");

        when(repository.findById("file123")).thenReturn(Optional.of(fileDoc));

        assertThrows(FileNotOwnedException.class, () -> service.rename("user1", "file123", "new.txt"));
    }

    @Test
    void shouldDeleteOwnFile() {
        FileDocument fileDoc = new FileDocument();
        fileDoc.setUserId("user1");
        fileDoc.setLink(UUID.randomUUID().toString());

        when(repository.findById("file123")).thenReturn(Optional.of(fileDoc));

        assertDoesNotThrow(() -> service.delete("user1", "file123"));
    }

    @Test
    void shouldRejectDeleteByOtherUser() {
        FileDocument fileDoc = new FileDocument();
        fileDoc.setUserId("otherUser");

        when(repository.findById("file123")).thenReturn(Optional.of(fileDoc));

        assertThrows(FileNotOwnedException.class, () -> service.delete("user1", "file123"));
    }
}
