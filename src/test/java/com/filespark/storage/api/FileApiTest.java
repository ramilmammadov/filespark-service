package com.filespark.storage.api;

import com.filespark.storage.StorageApplication;
import com.filespark.storage.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = StorageApplication.class)
@AutoConfigureMockMvc
class FileApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @Configuration
    static class TestConfig {
        @Bean
        public Validator validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @Test
    void shouldListPublicFiles() throws Exception {
        mockMvc.perform(get("/files/public"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUploadFileViaApi() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .param("filename", "test.txt")
                        .param("visibility", "PUBLIC")
                        .param("fileType", "DOCUMENT")
                        .param("tags", "tag1", "tag2")
                        .header("X-User-Id", "user1"))
                .andExpect(status().isOk());
    }


    @Test
    void shouldRenameFileViaApi() throws Exception {
        mockMvc.perform(put("/files/file123/rename")
                        .param("filename", "newname.txt")
                        .header("X-User-Id", "user1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteFileViaApi() throws Exception {
        mockMvc.perform(delete("/files/file123")
                        .header("X-User-Id", "user1"))
                .andExpect(status().isNoContent());
    }
}
