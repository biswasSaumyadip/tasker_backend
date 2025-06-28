package com.event.tasker.util;

import static org.mockito.ArgumentMatchers.any;

import org.springframework.jdbc.core.ResultSetExtractor;

public class MockUtils {
  @SuppressWarnings("unchecked")
  public static <T> ResultSetExtractor<T> anyResultSetExtractor() {
    return (ResultSetExtractor<T>) any(ResultSetExtractor.class);
  }
}
