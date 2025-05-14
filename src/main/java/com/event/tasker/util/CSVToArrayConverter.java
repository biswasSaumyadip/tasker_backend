package com.event.tasker.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVToArrayConverter {
  public static <T> List<T> convertCommaSeparated(String dbString, Function<String, T> converter) {
    if (dbString == null || dbString.trim().isEmpty()) {
      return Collections.emptyList();
    }

    return Arrays.stream(dbString.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(
            str -> {
              try {
                return converter.apply(str);
              } catch (Exception e) {
                log.warn("Failed to convert value: {}", str, e);
                return null;
              }
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
