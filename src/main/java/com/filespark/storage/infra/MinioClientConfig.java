package com.filespark.storage.infra;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioClientConfig {

    @Value("${minio.endpoint}")
    private String minioEndpointUrl;

    @Value("${minio.access-key}")
    private String minioUserKey;

    @Value("${minio.secret-key}")
    private String minioUserSecret;

    @Value("${minio.bucket}")
    private String bucketName;

    public String getBucketName() {
        return bucketName;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioEndpointUrl)
                .credentials(minioUserKey, minioUserSecret)
                .build();
    }
}
