package com.filespark.storage.api;

import com.filespark.storage.domain.enums.FileType;
import com.filespark.storage.domain.enums.Visibility;
import com.filespark.storage.model.request.FileUploadRequest;
import com.filespark.storage.model.response.FileInfoResponse;
import com.filespark.storage.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileApi {

    private final FileStorageService fileService;

    public FileApi(FileStorageService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFile(
            @RequestHeader("X-User-Id") String userId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("filename") String filename,
            @RequestParam("visibility") Visibility visibility,
            @RequestParam("fileType") FileType fileType,
            @Parameter(
                    name = "tags",
                    description = "Up to 5 tags",
                    in = ParameterIn.QUERY,
                    example = "tag1,tag2"
            )
            @RequestParam(value = "tags", required = false) List<String> tags) {

        if (tags != null && tags.size() > 5) {
            return ResponseEntity.badRequest().body("You can provide at most 5 tags.");
        }
        FileUploadRequest metadata = new FileUploadRequest(filename, fileType, visibility, tags);
        return ResponseEntity.ok(fileService.upload(userId, file, metadata));
    }

    @Operation(summary = "Rename a file")
    @PutMapping("/{id}/rename")
    public ResponseEntity<FileInfoResponse> renameFile(
            @RequestHeader("X-User-Id") @NotBlank String userId,
            @PathVariable String id,
            @RequestParam @NotBlank String filename) {
        return ResponseEntity.ok(fileService.rename(userId, id, filename));
    }

    @Operation(summary = "List all public files")
    @GetMapping("/public")
    public ResponseEntity<Page<FileInfoResponse>> listPublicFiles(
            @RequestParam(required = false) String tag,
            Pageable pageable) {
        return ResponseEntity.ok(fileService.listPublicFiles(tag, pageable));
    }

    @Operation(summary = "List files uploaded by the user")
    @GetMapping("/user")
    public ResponseEntity<Page<FileInfoResponse>> listUserFiles(
            @RequestHeader("X-User-Id") @NotBlank String userId,
            @RequestParam(required = false) String tag,
            Pageable pageable) {
        return ResponseEntity.ok(fileService.listUserFiles(userId, tag, pageable));
    }

    @Operation(summary = "Download a file by its unique link")
    @GetMapping("/download/{link}")
    public ResponseEntity<?> downloadFile(@PathVariable String link) {
        return fileService.download(link);
    }

    @Operation(summary = "Delete a file owned by the user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(
            @RequestHeader("X-User-Id") @NotBlank String userId,
            @PathVariable String id) {
        fileService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
