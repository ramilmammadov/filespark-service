package com.filespark.storage.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileTypeDetector {

    private static final Tika tika = new Tika();
    public static String detect(MultipartFile file) {
        try {
            String detected = tika.detect(file.getInputStream(), file.getOriginalFilename());
            return detected != null ? detected : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
