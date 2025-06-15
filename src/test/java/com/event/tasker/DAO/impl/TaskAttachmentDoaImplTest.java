package com.event.tasker.DAO.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.event.tasker.model.Attachment;
import com.event.tasker.rowMapper.AttachmentRowMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskAttachmentDoaImpl")
public class TaskAttachmentDoaImplTest {

  @InjectMocks TaskAttachmentDaoImpl taskDao;

  @Mock NamedParameterJdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("createAttachment: should return attachment ID when insertion succeeds")
  void testCreateAttachmentSuccess() {
    // Arrange
    Attachment attachment =
        Attachment.builder()
            .id("att-1")
            .taskId("task-1")
            .url("https://example.com/file.pdf")
            .fileName("file.pdf")
            .fileType("application/pdf")
            .build();

    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

    // Act
    String result = taskDao.createAttachment(attachment);

    // Assert
    assertNotNull(result, "Returned attachment ID should not be null");
    assertEquals("att-1", result, "Returned ID should match the attachment ID");
    verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  @DisplayName("createAttachment: should throw RuntimeException if 0 rows are affected")
  void testCreateAttachmentReturnsNullIfInsertFails() {
    // Arrange
    Attachment attachment =
        Attachment.builder()
            .id("att-2")
            .taskId("task-1")
            .url("https://example.com/file2.pdf")
            .fileName("file2.pdf")
            .fileType("application/pdf")
            .build();

    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class))).thenReturn(0);

    // Act & Assert
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> taskDao.createAttachment(attachment),
            "Should throw RuntimeException if insert fails");

    assertEquals("Error creating attachment", exception.getMessage());
    verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  @DisplayName("createAttachment: should propagate DataAccessException")
  void testCreateAttachmentThrowsDataAccessException() {
    // Arrange
    Attachment attachment =
        Attachment.builder()
            .id("att-3")
            .taskId("task-1")
            .url("https://example.com/file3.pdf")
            .fileName("file3.pdf")
            .fileType("application/pdf")
            .build();

    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
        .thenThrow(new DataAccessException("DB error") {});

    // Act & Assert
    DataAccessException exception =
        assertThrows(DataAccessException.class, () -> taskDao.createAttachment(attachment));

    assertTrue(exception.getMessage().contains("DB error"));
    verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  @DisplayName("createAttachment: should wrap unexpected exceptions in RuntimeException")
  void testCreateAttachmentThrowsRuntimeExceptionOnOtherErrors() {
    // Arrange
    Attachment attachment =
        Attachment.builder()
            .id("att-4")
            .taskId("task-1")
            .url("https://example.com/file4.pdf")
            .fileName("file4.pdf")
            .fileType("application/pdf")
            .build();

    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
        .thenThrow(new IllegalStateException("Unexpected error"));

    // Act & Assert
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> taskDao.createAttachment(attachment));

    assertEquals("Unexpected error", exception.getMessage());
    verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  @DisplayName("Unit Test: getAttachment should return attachment when found")
  void testGetAttachment_Success() {
    // Arrange
    String taskId = "task-abc-123";
    Attachment mockAttachment =
        Attachment.builder()
            .url("https://example.com/file.pdf")
            .fileName("file.pdf")
            .fileType("application/pdf")
            .build();

    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(AttachmentRowMapper.class)))
        .thenReturn(mockAttachment);

    // Act
    Optional<Attachment> result = taskDao.getAttachment(taskId);

    // Assert
    assertTrue(result.isPresent(), "Attachment should be present");
    assertEquals("https://example.com/file.pdf", result.get().getUrl(), "URL should match");
    assertEquals("file.pdf", result.get().getFileName(), "File name should match");
    assertEquals("application/pdf", result.get().getFileType(), "File type should match");

    ArgumentCaptor<MapSqlParameterSource> paramCaptor =
        ArgumentCaptor.forClass(MapSqlParameterSource.class);
    verify(jdbcTemplate)
        .queryForObject(anyString(), paramCaptor.capture(), any(AttachmentRowMapper.class));
    assertEquals(
        taskId, paramCaptor.getValue().getValue("taskId"), "Should query with correct taskId");
  }

  @Test
  @DisplayName("Unit Test: getAttachment should return empty optional when not found")
  void testGetAttachment_NotFound() {
    // Arrange
    String taskId = "task-def-456";
    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(AttachmentRowMapper.class)))
        .thenThrow(new EmptyResultDataAccessException(1));

    // Act
    Optional<Attachment> result = taskDao.getAttachment(taskId);

    // Assert
    assertTrue(result.isEmpty(), "Should return an empty Optional when no attachment is found");
    verify(jdbcTemplate)
        .queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(AttachmentRowMapper.class));
  }

  @Test
  @DisplayName("Unit Test: getAttachment should return empty optional on database error")
  void testGetAttachment_DataAccessException() {
    // Arrange
    String taskId = "task-ghi-789";
    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(AttachmentRowMapper.class)))
        .thenThrow(new DataAccessException("Simulated database connection error") {});

    // Act
    Optional<Attachment> result = taskDao.getAttachment(taskId);

    // Assert
    assertTrue(result.isEmpty(), "Should return an empty Optional on a data access exception");
    verify(jdbcTemplate)
        .queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(AttachmentRowMapper.class));
  }
}
