package com.event.tasker.rowMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.event.tasker.model.Attachment;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: AttachmentRowMapper")
class AttachmentRowMapperTest {

  @Mock private ResultSet rs;

  private AttachmentRowMapper rowMapper;

  @BeforeEach
  void setUp() {
    rowMapper = new AttachmentRowMapper();
  }

  @Test
  @DisplayName("Should map ResultSet to Attachment object with all non-null fields")
  void mapRow_shouldReturnAttachment_whenAllFieldsArePresent() throws SQLException {
    when(rs.getString("id")).thenReturn("att-001");
    when(rs.getString("task_id")).thenReturn("task-001");
    when(rs.getString("url")).thenReturn("https://cdn.com/doc.pdf");
    when(rs.getString("file_name")).thenReturn("doc.pdf");
    when(rs.getString("file_type")).thenReturn("application/pdf");

    Attachment attachment = rowMapper.mapRow(rs, 1);

    assertNotNull(attachment);
    assertEquals("att-001", attachment.getId());
    assertEquals("task-001", attachment.getTaskId());
    assertEquals("https://cdn.com/doc.pdf", attachment.getUrl());
    assertEquals("doc.pdf", attachment.getFileName());
    assertEquals("application/pdf", attachment.getFileType());
  }

  @Test
  @DisplayName("Should map ResultSet to Attachment object with all null fields")
  void mapRow_shouldHandleNullFieldsGracefully() throws SQLException {
    when(rs.getString("id")).thenReturn(null);
    when(rs.getString("task_id")).thenReturn(null);
    when(rs.getString("url")).thenReturn(null);
    when(rs.getString("file_name")).thenReturn(null);
    when(rs.getString("file_type")).thenReturn(null);

    Attachment attachment = rowMapper.mapRow(rs, 1);

    assertNotNull(attachment);
    assertNull(attachment.getId());
    assertNull(attachment.getTaskId());
    assertNull(attachment.getUrl());
    assertNull(attachment.getFileName());
    assertNull(attachment.getFileType());
  }

  @Test
  @DisplayName("Should map ResultSet to Attachment object with empty string fields")
  void mapRow_shouldHandleEmptyStringFields() throws SQLException {
    when(rs.getString("id")).thenReturn("");
    when(rs.getString("task_id")).thenReturn("");
    when(rs.getString("url")).thenReturn("");
    when(rs.getString("file_name")).thenReturn("");
    when(rs.getString("file_type")).thenReturn("");

    Attachment attachment = rowMapper.mapRow(rs, 1);

    assertNotNull(attachment);
    assertEquals("", attachment.getId());
    assertEquals("", attachment.getTaskId());
    assertEquals("", attachment.getUrl());
    assertEquals("", attachment.getFileName());
    assertEquals("", attachment.getFileType());
  }
}
