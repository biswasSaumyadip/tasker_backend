package com.event.tasker.DAO.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.springframework.dao.DataAccessResourceFailureException;
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

  @Test
  @DisplayName("Unit Test: updateAttachment should return ID on successful update")
  void testUpdateAttachment_Success() {
    // Arrange
    Attachment attachment =
        Attachment.builder()
            .id("att-123")
            .taskId("task-abc")
            .url("https://example.com/new_document.docx")
            .fileName("new_document.docx")
            .fileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            .build();

    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    // Act
    String resultId = taskDao.updateAttachment(attachment);

    // Assert
    assertNotNull(resultId, "Result ID should not be null on successful update");
    assertEquals(attachment.getId(), resultId, "Returned ID should match the attachment's ID");

    ArgumentCaptor<MapSqlParameterSource> paramCaptor =
        ArgumentCaptor.forClass(MapSqlParameterSource.class);
    verify(jdbcTemplate).update(anyString(), paramCaptor.capture());

    MapSqlParameterSource capturedParams = paramCaptor.getValue();
    assertEquals(attachment.getUrl(), capturedParams.getValue("url"));
    assertEquals(attachment.getFileName(), capturedParams.getValue("fileName"));
    assertEquals(attachment.getFileType(), capturedParams.getValue("fileType"));
    assertEquals(attachment.getTaskId(), capturedParams.getValue("taskId"));
  }

  @Test
  @DisplayName("Unit Test: updateAttachment should return null when no row is updated")
  void testUpdateAttachment_NotFound() {
    // Arrange
    Attachment attachment =
        Attachment.builder()
            .id("att-456")
            .taskId("task-def-non-existent")
            .url("https://example.com/some_file.zip")
            .fileName("some_file.zip")
            .fileType("application/zip")
            .build();

    // Simulate that no rows were affected by the update
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(0);

    // Act
    String resultId = taskDao.updateAttachment(attachment);

    // Assert
    assertNull(resultId, "Should return null when no attachment is found to update");
    verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  @DisplayName("Unit Test: updateAttachment should throw DataAccessException on DB error")
  void testUpdateAttachment_DataAccessException() {
    // Arrange
    Attachment attachment =
        Attachment.builder()
            .id("att-789")
            .taskId("task-ghi")
            .url("https://example.com/report.csv")
            .fileName("report.csv")
            .fileType("text/csv")
            .build();

    DataAccessException dbException = new DataAccessException("Simulated DB Error") {};
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenThrow(dbException);

    // Act & Assert
    DataAccessException thrown =
        assertThrows(
            DataAccessException.class,
            () -> taskDao.updateAttachment(attachment),
            "Should re-throw DataAccessException on database error");

    assertEquals(dbException, thrown, "The thrown exception should be the one from the mock");
    verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  @DisplayName("should delete attachment by taskId when taskId is provided")
  void shouldDeleteAttachmentByTaskId() {
    // given
    String taskId = "123";
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    // when
    String result = taskDao.deleteAttachment(null, taskId);

    // then
    assertEquals(taskId, result);
  }

  @Test
  @DisplayName("should delete attachment by id when only id is provided")
  void shouldDeleteAttachmentById() {
    // given
    String id = "attach-123";
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    // when
    String result = taskDao.deleteAttachment(id, null);

    // then
    assertEquals(id, result);
  }

  @Test
  @DisplayName("should return empty string when both id and taskId are null")
  void shouldReturnEmptyStringWhenIdAndTaskIdAreNull() {
    // when
    String result = taskDao.deleteAttachment(null, null);

    // then
    assertEquals("", result);
  }

  @Test
  @DisplayName("should throw exception when jdbcTemplate throws DataAccessException")
  void shouldThrowExceptionWhenJdbcCallFails() {
    // given
    String id = "fail-123";
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class)))
        .thenThrow(new DataAccessResourceFailureException("DB error"));

    // when / then
    assertThrows(DataAccessException.class, () -> taskDao.deleteAttachment(id, null));
  }

  @Test
  @DisplayName("softDeleteAttachment: Should successfully delete and return ID")
  void testSoftDeleteAttachment_ShouldSuccessfullyDeleteAndReturnTheID() {
    String attachmentId = "att-456";
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    String id = taskDao.softDeleteAttachment(attachmentId);

    assertEquals(attachmentId, id);
    verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  @DisplayName("softDeleteAttachment: Should throw DataAccessException")
  void testSoftDeleteAttachment_ShouldThrowDataAccessException() {
    String id = "att-456";

    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class)))
        .thenThrow(new DataAccessResourceFailureException("DB error"));

    assertThrows(DataAccessException.class, () -> taskDao.softDeleteAttachment(id));

    verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
  }
}
