package com.tifawinkitchen.recipeapp.service;


import com.tifawinkitchen.recipeapp.service.impl.FileStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private FileStorageServiceImpl fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl(tempDir.toString());
    }

    @Test
    void storeFile_Success() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "test.txt", "test.txt", "text/plain", "Test content".getBytes());

        String fileName = fileStorageService.storeFile(file);

        assertNotNull(fileName);
        assertTrue(Files.exists(tempDir.resolve(fileName)));
    }

    @Test
    void storeFile_InvalidPath_ThrowsException() {
        MultipartFile file = new MockMultipartFile(
                "../test.txt", "../test.txt", "text/plain", "Test content".getBytes());

        assertThrows(RuntimeException.class, () -> fileStorageService.storeFile(file));
    }

    @Test
    void loadFileAsResource_Success() throws Exception {
        String testContent = "Test content";
        String fileName = "test.txt";
        Files.write(tempDir.resolve(fileName), testContent.getBytes());

        Resource resource = fileStorageService.loadFileAsResource(fileName);

        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void loadFileAsResource_NotFound_ThrowsException() {
        assertThrows(RuntimeException.class, () -> fileStorageService.loadFileAsResource("nonexistent.txt"));
    }
}