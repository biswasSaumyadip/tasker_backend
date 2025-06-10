package com.event.tasker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.event.tasker.model.Attachment;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: LocalFileStorageService")
class LocalFileStorageServiceTest {

  @TempDir Path tempDir;

  @Mock private MultipartFile mockFile;

  @InjectMocks private LocalFileStorageService fileStorageService;

  @BeforeEach
  void setUp() {
    // Set the upload directory to our temporary directory
    ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
  }

  @Test
  @DisplayName("init: should create directory if it doesn't exist")
  void testInitCreatesDirectory() {
    // Act
    fileStorageService.init();

    // Assert
    Path fileStorageLocation =
        (Path) ReflectionTestUtils.getField(fileStorageService, "fileStorageLocation");
    assertNotNull(fileStorageLocation);
    assertTrue(Files.exists(fileStorageLocation));
    assertEquals(tempDir.toAbsolutePath().normalize(), fileStorageLocation);
  }

  @Test
  @DisplayName("init: should throw RuntimeException if directory creation fails")
  void testInitThrowsExceptionOnDirectoryCreationFailure() throws Exception {
    // Arrange - use a path that cannot be created
    String invalidPath = "\0invalid"; // Null character makes this path invalid

    // Create a new service instance with the invalid path
    LocalFileStorageService invalidService = new LocalFileStorageService();
    ReflectionTestUtils.setField(invalidService, "uploadDir", invalidPath);

    // Act & Assert
    assertThrows(RuntimeException.class, () -> invalidService.init());
  }

  @Test
  @DisplayName("uploadFile: should upload file successfully and return unique filename")
  void testUploadFileSuccess() throws IOException {
    // Arrange
    String originalFilename = "test-file.txt";
    when(mockFile.getOriginalFilename()).thenReturn(originalFilename);

    InputStream inputStream = mock(InputStream.class);
    when(mockFile.getInputStream()).thenReturn(inputStream);

    // Initialize the service to create the directory
    fileStorageService.init();

    // Act
    String result = fileStorageService.uploadFile(mockFile);

    // Assert
    assertNotNull(result);
    assertTrue(result.endsWith("_" + originalFilename));
    assertTrue(result.length() > originalFilename.length()); // Should have UUID prefix
  }

  @Test
  @DisplayName("uploadFile: should throw IOException for invalid file path")
  void testUploadFileThrowsExceptionForInvalidPath() {
    // Arrange
    when(mockFile.getOriginalFilename()).thenReturn("../invalid/path.txt");

    // Initialize the service to create the directory
    fileStorageService.init();

    // Act & Assert
    assertThrows(IOException.class, () -> fileStorageService.uploadFile(mockFile));
  }

  @Test
  @DisplayName("uploadFile: should throw IOException when file input stream fails")
  void testUploadFileThrowsExceptionOnStreamFailure() throws IOException {
    // Arrange
    when(mockFile.getOriginalFilename()).thenReturn("test-file.txt");
    when(mockFile.getInputStream()).thenThrow(new IOException("Stream error"));

    // Initialize the service to create the directory
    fileStorageService.init();

    // Act & Assert
    assertThrows(IOException.class, () -> fileStorageService.uploadFile(mockFile));
  }

  @Test
  @DisplayName("getFileMetadata: should return null as it's not implemented")
  void testGetFileMetadataReturnsNull() throws IOException {
    // Act
    Attachment result = fileStorageService.getFileMetadata("file-id");

    // Assert
    assertEquals(null, result);
  }

  @Test
  @DisplayName("getFile: should return resource when file exists")
  void testGetFileSuccess() throws IOException {
    // Arrange
    String filename = "test-file.txt";
    Path filePath = tempDir.resolve(filename);
    Files.createFile(filePath);

    // Initialize the service to create the directory
    fileStorageService.init();

    // Act
    Resource result = fileStorageService.getFile(filename);

    // Assert
    assertNotNull(result);
    assertTrue(result.exists());
    assertEquals(filePath.toUri(), result.getURI());
  }

  @Test
  @DisplayName("getFile: should throw FileNotFoundException when file doesn't exist")
  void testGetFileThrowsExceptionWhenFileNotFound() {
    // Arrange
    String filename = "non-existent-file.txt";

    // Initialize the service to create the directory
    fileStorageService.init();

    // Act & Assert
    assertThrows(FileNotFoundException.class, () -> fileStorageService.getFile(filename));
  }

  @Test
  @DisplayName("deleteFile: should delete file when it exists")
  void testDeleteFileSuccess() throws IOException {
    // Arrange
    String filename = "test-file-to-delete.txt";
    Path filePath = tempDir.resolve(filename);
    Files.createFile(filePath);

    // Initialize the service to create the directory
    fileStorageService.init();

    // Act
    fileStorageService.deleteFile(filename);

    // Assert
    assertTrue(Files.notExists(filePath));
  }

  @Test
  @DisplayName("deleteFile: should not throw exception when file doesn't exist")
  void testDeleteFileNoExceptionWhenFileNotFound() throws IOException {
    // Arrange
    String filename = "non-existent-file.txt";

    // Initialize the service to create the directory
    fileStorageService.init();

    // Act & Assert - should not throw exception
    fileStorageService.deleteFile(filename);
  }
}
