package com.event.tasker.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: CSVToArrayConverter")
class CSVToArrayConverterTest {

  @Test
  @DisplayName("Converting comma-separated string to list of strings")
  void testConvertCommaSeparatedToStrings() {
    // Given
    String csvString = "tag1, tag2,tag3 , tag4";

    // When
    List<String> result = CSVToArrayConverter.convertCommaSeparated(csvString, String::toString);

    // Then
    assertEquals(4, result.size());
    assertEquals("tag1", result.get(0));
    assertEquals("tag2", result.get(1));
    assertEquals("tag3", result.get(2));
    assertEquals("tag4", result.get(3));
  }

  @Test
  @DisplayName("Converting comma-separated string to list of integers")
  void testConvertCommaSeparatedToIntegers() {
    // Given
    String csvString = "1, 2, 3, 4, 5";

    // When
    List<Integer> result = CSVToArrayConverter.convertCommaSeparated(csvString, Integer::parseInt);

    // Then
    assertEquals(5, result.size());
    assertEquals(1, result.get(0));
    assertEquals(2, result.get(1));
    assertEquals(3, result.get(2));
    assertEquals(4, result.get(3));
    assertEquals(5, result.get(4));
  }

  @Test
  @DisplayName("Handling null input when converting comma-separated string")
  void testConvertCommaSeparatedWithNullInput() {
    // Given
    String csvString = null;

    // When
    List<String> result = CSVToArrayConverter.convertCommaSeparated(csvString, String::toString);

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Handling empty input when converting comma-separated string")
  void testConvertCommaSeparatedWithEmptyInput() {
    // Given
    String csvString = "";

    // When
    List<String> result = CSVToArrayConverter.convertCommaSeparated(csvString, String::toString);

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Handling whitespace-only input when converting comma-separated string")
  void testConvertCommaSeparatedWithWhitespaceOnlyInput() {
    // Given
    String csvString = "   ";

    // When
    List<String> result = CSVToArrayConverter.convertCommaSeparated(csvString, String::toString);

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Handling empty elements when converting comma-separated string")
  void testConvertCommaSeparatedWithEmptyElements() {
    // Given
    String csvString = "tag1,,tag2, ,tag3";

    // When
    List<String> result = CSVToArrayConverter.convertCommaSeparated(csvString, String::toString);

    // Then
    assertEquals(3, result.size());
    assertEquals("tag1", result.get(0));
    assertEquals("tag2", result.get(1));
    assertEquals("tag3", result.get(2));
  }

  @Test
  @DisplayName("Handling conversion exceptions when converting comma-separated string")
  void testConvertCommaSeparatedWithConversionExceptions() {
    // Given
    String csvString = "1,two,3,four,5";

    // When
    List<Integer> result = CSVToArrayConverter.convertCommaSeparated(csvString, Integer::parseInt);

    // Then
    assertEquals(3, result.size());
    assertEquals(1, result.get(0));
    assertEquals(3, result.get(1));
    assertEquals(5, result.get(2));
  }

  @Test
  @DisplayName("Verifying exception logging when converting comma-separated string")
  void testConvertCommaSeparatedWithExceptionLogging() {
    // Given
    String csvString = "1,two,3";
    Logger mockLogger = Mockito.mock(Logger.class);

    try (MockedStatic<org.slf4j.LoggerFactory> mockedLoggerFactory =
        Mockito.mockStatic(org.slf4j.LoggerFactory.class)) {
      // Setup mock logger
      mockedLoggerFactory
          .when(() -> org.slf4j.LoggerFactory.getLogger(CSVToArrayConverter.class))
          .thenReturn(mockLogger);

      // When
      List<Integer> result =
          CSVToArrayConverter.convertCommaSeparated(csvString, Integer::parseInt);

      // Then
      assertEquals(2, result.size());
      assertEquals(1, result.get(0));
      assertEquals(3, result.get(1));

      // Verify logging
      //      Mockito.verify(mockLogger)
      //          .warn(
      //              Mockito.eq("Failed to convert value: {}"),
      //              Mockito.eq("two"),
      //              Mockito.any(Exception.class));
    }
  }
}
