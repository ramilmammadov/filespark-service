package com.filespark.storage.model.request;

import com.filespark.storage.domain.enums.FileType;
import com.filespark.storage.domain.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    private String filename;
    private FileType fileType;
    private Visibility visibility;
    private List<String> tags;
}