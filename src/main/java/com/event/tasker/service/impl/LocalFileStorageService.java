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

    String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

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
  public Attachment getFileMetadata(String fileId) {
    return null;
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
