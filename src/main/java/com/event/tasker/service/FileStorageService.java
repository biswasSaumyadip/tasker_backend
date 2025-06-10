package com.event.tasker.service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.event.tasker.model.Attachment;

public interface FileStorageService {
  String uploadFile(MultipartFile file) throws IOException; // returns file identifier or URL

  Attachment getFileMetadata(String fileId);

  Resource getFile(String fileId) throws IOException;

  void deleteFile(String fileId) throws IOException;
}
