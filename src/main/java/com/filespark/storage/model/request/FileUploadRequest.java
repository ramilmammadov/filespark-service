package com.filespark.storage.model.request;

import com.filespark.storage.domain.enums.FileType;
import com.filespark.storage.domain.enums.Visibility;
import jakarta.validation.constraints.Size;
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

    @Size(max = 5, message = "Maximum 5 tags are allowed")
    private List<String> tags;
}