package com.event.tasker.util;

import java.nio.file.Paths;
import java.util.UUID;

/** Utility class for working with file uploads. */
public class FileUtils {

  /**
   * Sanitizes a file name to prevent directory traversal attacks, remove unsafe characters, and
   * ensure uniqueness.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Strips path information
   *   <li>Removes dangerous characters
   *   <li>Preserves file extension
   *   <li>Appends UUID for uniqueness
   * </ul>
   *
   * @param originalFilename the original filename from MultipartFile
   * @return sanitized and unique filename
   * @throws IllegalArgumentException if the filename is null
   */
  public static String sanitizeFileName(String originalFilename) {
    if (originalFilename == null) {
      throw new IllegalArgumentException("Original filename must not be null");
    }

    // Strip path components
    String originalName = Paths.get(originalFilename).getFileName().toString();

    // Extract extension (if any)
    int dotIndex = originalName.lastIndexOf('.');
    String ext = (dotIndex != -1) ? originalName.substring(dotIndex) : "";

    // Extract base name
    String baseName = (dotIndex != -1) ? originalName.substring(0, dotIndex) : originalName;

    // Replace dangerous characters in base name
    String safeBaseName = baseName.replaceAll("[^a-zA-Z0-9_-]", "_");

    // Return sanitized filename with UUID for uniqueness
    return safeBaseName + "_" + UUID.randomUUID() + ext;
  }
}
