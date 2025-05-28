package com.event.tasker.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FileUtilsTest {

  @Test
  @DisplayName("Unit Test: sanitizeFileName should clean unsafe characters and preserve extension")
  void testSanitizeFileName_NormalCase() {
    String result = FileUtils.sanitizeFileName("user report.pdf");

    assertNotNull(result);
    assertTrue(result.matches("user_report_[a-zA-Z0-9\\-]+\\.pdf"));
  }

  @Test
  @DisplayName("Unit Test: sanitizeFileName should handle path traversal attempts")
  void testSanitizeFileName_PathTraversal() {
    String result = FileUtils.sanitizeFileName("../../etc/passwd");

    assertNotNull(result);
    assertTrue(result.matches("passwd_[a-zA-Z0-9\\-]+"));
  }

  @Test
  @DisplayName("Unit Test: sanitizeFileName should handle emoji and symbols")
  void testSanitizeFileName_SpecialChars() {
    String result = FileUtils.sanitizeFileName("💣💥🔥file💩.js");

    assertNotNull(result);
    assertTrue(result.matches("___file__[a-zA-Z0-9\\-]+\\.js"));
  }

  @Test
  @DisplayName("Unit Test: sanitizeFileName should handle filename with no extension")
  void testSanitizeFileName_NoExtension() {
    String result = FileUtils.sanitizeFileName("LICENSE");

    assertNotNull(result);
    assertTrue(result.matches("LICENSE_[a-zA-Z0-9\\-]+"));
  }

  @Test
  @DisplayName("Unit Test: sanitizeFileName should throw IllegalArgumentException on null input")
  void testSanitizeFileName_NullInput() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> FileUtils.sanitizeFileName(null));
    assertEquals("Original filename must not be null", ex.getMessage());
  }
}
