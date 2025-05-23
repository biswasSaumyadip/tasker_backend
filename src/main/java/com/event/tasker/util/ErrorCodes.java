package com.event.tasker.util;

public class ErrorCodes {
  // Database
  public static final String DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
  public static final String DB_ERROR = "DB_ERROR";
  public static final String DUPLICATE_ENTRY = "DUPLICATE_ENTRY";
  public static final String DB_CONSTRAINT_VIOLATION = "DB_CONSTRAINT_VIOLATION";
  // Validation
  public static final String VALIDATION_FAILED = "VALIDATION_FAILED";

  // HTTP related
  public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
  public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";

  // Generic
  public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
}
