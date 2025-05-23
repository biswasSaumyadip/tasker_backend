package com.event.tasker.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.event.tasker.model.TaskerResponse;
import com.event.tasker.util.ErrorCodes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalException {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<TaskerResponse<Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return new ResponseEntity<>(
        TaskerResponse.builder().message(ex.getMessage()).build(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<TaskerResponse<Object>> handleDatabaseError(DataAccessException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            TaskerResponse.failure(
                "A database error occurred. Please try again later.", ErrorCodes.DB_ERROR));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<TaskerResponse<Object>> handleConstraintViolation(
      DataIntegrityViolationException ex) {

    String message = "Data integrity error: " + ex.getMostSpecificCause().getMessage();

    if (ex.getMostSpecificCause().getMessage().contains("unique")
        || ex.getMostSpecificCause().getMessage().contains("duplicate")) {
      message = "The value already exists. Please use a different one.";
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(TaskerResponse.failure(message, ErrorCodes.DB_CONSTRAINT_VIOLATION));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<TaskerResponse<Object>> handleMethodNotAllowed(
      HttpRequestMethodNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(
            TaskerResponse.failure("Request method not supported", ErrorCodes.METHOD_NOT_ALLOWED));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<TaskerResponse<Object>> handleAll(Exception ex) {
    log.error("Exception occurred: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            TaskerResponse.failure(
                "An unexpected error occurred", ErrorCodes.INTERNAL_SERVER_ERROR));
  }
}
