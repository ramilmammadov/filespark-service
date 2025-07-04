package com.filespark.storage.model.response;

import com.filespark.storage.domain.FileDocument;
import com.filespark.storage.domain.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoResponse {
    private String id;
    private String filename;
    private String contentType;
    private long size;
    private String link;
    private Visibility visibility;
    private List<String> tags;
    private Instant uploadedAt;

    public static FileInfoResponse from(FileDocument doc) {
        return FileInfoResponse.builder()
                .id(doc.getId())
                .filename(doc.getFilename())
                .contentType(doc.getContentType())
                .size(doc.getSize())
                .link(doc.getLink())
                .visibility(doc.getVisibility())
                .tags(doc.getTags())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}
