package com.event.tasker.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.event.tasker.model.Attachment;
import com.event.tasker.service.FileStorageService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

  @Value("${file.upload-dir}")
  private String uploadDir;

  private Path fileStorageLocation;

  @PostConstruct
  public void init() {
    this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      log.error("Could not create the upload directory!", ex);
      throw new RuntimeException("Could not create the upload directory!", ex);
    }
  }

  @Override
  public String uploadFile(MultipartFile file) throws IOException {
    String originalFileName =
        StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

    if (originalFileName.contains("..")) {
      throw new IOException("Invalid file path sequence in filename: " + originalFileName);
    }

    String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

    // Note: We now use the 'fileStorageLocation' field which is already an absolute, normalized
    // path
    Path targetPath = this.fileStorageLocation.resolve(uniqueFileName);

    try (InputStream inputStream = file.getInputStream()) {
      Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
      log.info("File uploaded successfully: {}", uniqueFileName);
    }

    return uniqueFileName;
  }

  @Override
  public Attachment getFileMetadata(String fileId) throws IOException {

    try {
      Path filePath = this.fileStorageLocation.resolve(fileId).normalize();

      if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
        throw new FileNotFoundException("Could not read file from disk: " + fileId);
      }

      // Create the resource object from the file path
      Resource resource = new UrlResource(filePath.toUri());

      // Probe the file to determine its content type (e.g., "image/png")
      String contentType = Files.probeContentType(filePath);

      // If the OS can't determine the type, provide a default
      if (contentType == null) {
        contentType = "application/octet-stream";
      }

      log.info("Successfully loaded file {} from disk with content type {}", fileId, contentType);

      return Attachment.builder()
          .fileType(contentType)
          .fileName(fileId)
          .url(resource.getURL().toString())
          .build();
    } catch (FileNotFoundException ex) {
      log.error("Could not load file from disk: {}", fileId, ex);
      return null;
    }
  }

  @Override
  public Resource getFile(String fileId) throws IOException {
    Path filePath = this.fileStorageLocation.resolve(fileId).normalize();
    log.info("Loading file from path: {}", filePath);
    UrlResource resource = new UrlResource(filePath.toUri());
    if (resource.exists() && resource.isReadable()) {
      return resource;
    } else {
      throw new FileNotFoundException("Could not read file: " + fileId);
    }
  }

  @Override
  public void deleteFile(String fileId) throws IOException {
    Path filePath = this.fileStorageLocation.resolve(fileId).normalize();
    log.warn("Attempting to delete file: {}", filePath);
    Files.deleteIfExists(filePath);
  }
}
