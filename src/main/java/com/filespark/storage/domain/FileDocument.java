package com.filespark.storage.domain;

import com.filespark.storage.domain.enums.Visibility;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("files")
public class FileDocument {
    @Id
    private String id;
    private String userId;
    private String filename;
    private String contentType;
    private long size;
    private String link;
    private Visibility visibility;
    private List<String> tags;
    private Instant uploadedAt;
    private String contentHash;
}
