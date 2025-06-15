package com.event.tasker.rowMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.event.tasker.model.Attachment;
import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.util.CSVToArrayConverter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskDetailRowMapper")
class TaskDetailRowMapperTest {

  @Mock private ResultSet mockResultSet;
  @Mock private Gson mockGson;

  private TaskDetailRowMapper rowMapper;

  @BeforeEach
  void setUp() {
    rowMapper = new TaskDetailRowMapper(mockGson);
  }

  @Test
  @DisplayName("Should map ResultSet row to TaskDetail correctly with attachments and tags")
  void testMapRow_HappyPath() throws SQLException {
    String id = "task-123";
    String title = "Test Task";
    String description = "Description here";
    boolean completed = true;
    String priority = "MEDIUM";
    String assignedTo = "Alice Bob";
    Instant dueDate = Instant.now();
    Timestamp dueTimestamp = Timestamp.from(dueDate);
    String parentId = "parent-1";
    String tagsString = "tag1, tag2, tag3";
    String attachmentsJson =
        """
        [
          {"url":"http://url1","fileName":"file1","fileType":"pdf"},
          {"url":"http://url2","fileName":"file2","fileType":"doc"}
        ]
        """;

    // Mock ResultSet getters
    when(mockResultSet.getString("id")).thenReturn(id);
    when(mockResultSet.getString("title")).thenReturn(title);
    when(mockResultSet.getString("description")).thenReturn(description);
    when(mockResultSet.getBoolean("completed")).thenReturn(completed);
    when(mockResultSet.getString("priority")).thenReturn(priority);
    when(mockResultSet.getString("assignedTo")).thenReturn(assignedTo);
    when(mockResultSet.getTimestamp("dueDate")).thenReturn(dueTimestamp);
    when(mockResultSet.getString("parentId")).thenReturn(parentId);
    when(mockResultSet.getString("tags")).thenReturn(tagsString);
    when(mockResultSet.getString("attachments")).thenReturn(attachmentsJson);

    // Mock CSVToArrayConverter to return list of tags
    try (MockedStatic<CSVToArrayConverter> mockedCSV =
        Mockito.mockStatic(CSVToArrayConverter.class)) {
      List<String> tagList = List.of("tag1", "tag2", "tag3");
      mockedCSV
          .when(() -> CSVToArrayConverter.convertCommaSeparated(eq(tagsString), any()))
          .thenReturn(tagList);

      // Mock Gson parsing of attachments JSON
      JsonArray mockJsonArray = new JsonArray();
      JsonElement element1 = Mockito.mock(JsonElement.class);
      JsonElement element2 = Mockito.mock(JsonElement.class);
      mockJsonArray.add(element1);
      mockJsonArray.add(element2);

      when(mockGson.fromJson(attachmentsJson, JsonArray.class)).thenReturn(mockJsonArray);

      Attachment attachment1 =
          Attachment.builder().url("http://url1").fileName("file1").fileType("pdf").build();
      Attachment attachment2 =
          Attachment.builder().url("http://url2").fileName("file2").fileType("doc").build();

      when(mockGson.fromJson(element1, Attachment.class)).thenReturn(attachment1);
      when(mockGson.fromJson(element2, Attachment.class)).thenReturn(attachment2);

      // Act
      TaskDetail result = rowMapper.mapRow(mockResultSet, 1);

      // Assert
      assertNotNull(result);
      assertEquals(id, result.getId());
      assertEquals(title, result.getTitle());
      assertEquals(description, result.getDescription());
      assertEquals(completed, result.isCompleted());
      assertEquals(Task.Priority.MEDIUM, result.getPriority());
      assertEquals(assignedTo, result.getAssignedTo());
      assertEquals(dueDate, result.getDueDate());
      assertEquals(parentId, result.getParentId());
      assertEquals(tagList, result.getTags());

      assertNotNull(result.getAttachments());
      assertEquals(2, result.getAttachments().size());
      assertEquals(attachment1, result.getAttachments().get(0));
      assertEquals(attachment2, result.getAttachments().get(1));
    }
  }

  @Test
  @DisplayName("Should handle null or empty tags and attachments gracefully")
  void testMapRow_NullOrEmptyTagsAttachments() throws SQLException {
    when(mockResultSet.getString("tags")).thenReturn(null);
    when(mockResultSet.getString("attachments")).thenReturn(null);
    when(mockResultSet.getString("id")).thenReturn("id");
    when(mockResultSet.getString("title")).thenReturn("title");
    when(mockResultSet.getString("description")).thenReturn("desc");
    when(mockResultSet.getBoolean("completed")).thenReturn(false);
    when(mockResultSet.getString("priority")).thenReturn("LOW");
    when(mockResultSet.getString("assignedTo")).thenReturn("someone");
    when(mockResultSet.getTimestamp("dueDate")).thenReturn(Timestamp.from(Instant.now()));
    when(mockResultSet.getString("parentId")).thenReturn(null);

    try (MockedStatic<CSVToArrayConverter> mockedCSV =
        Mockito.mockStatic(CSVToArrayConverter.class)) {
      mockedCSV
          .when(() -> CSVToArrayConverter.convertCommaSeparated(eq(null), any()))
          .thenReturn(Collections.emptyList());

      TaskDetail result = rowMapper.mapRow(mockResultSet, 1);

      assertNotNull(result);
      assertTrue(result.getTags().isEmpty());
      assertNotNull(result.getAttachments());
      assertTrue(result.getAttachments().isEmpty());
    }
  }

  @Test
  @DisplayName("Should log error and return empty attachments list if JSON parsing fails")
  void testMapRow_AttachmentsJsonSyntaxException() throws SQLException {
    String badJson = "[{bad json}]";

    when(mockResultSet.getString("attachments")).thenReturn(badJson);
    when(mockResultSet.getString("id")).thenReturn("task-err");
    when(mockResultSet.getString("tags")).thenReturn(null);
    when(mockResultSet.getString("title")).thenReturn("title");
    when(mockResultSet.getString("description")).thenReturn("desc");
    when(mockResultSet.getBoolean("completed")).thenReturn(false);
    when(mockResultSet.getString("priority")).thenReturn("LOW");
    when(mockResultSet.getString("assignedTo")).thenReturn("user");
    when(mockResultSet.getTimestamp("dueDate")).thenReturn(Timestamp.from(Instant.now()));
    when(mockResultSet.getString("parentId")).thenReturn(null);

    try (MockedStatic<CSVToArrayConverter> mockedCSV =
        Mockito.mockStatic(CSVToArrayConverter.class)) {
      mockedCSV
          .when(() -> CSVToArrayConverter.convertCommaSeparated(eq(null), any()))
          .thenReturn(Collections.emptyList());

      when(mockGson.fromJson(badJson, JsonArray.class))
          .thenThrow(new JsonSyntaxException("bad json"));

      TaskDetail result = rowMapper.mapRow(mockResultSet, 1);

      // It should not throw but log error internally, attachments list empty
      assertNotNull(result);
      assertTrue(result.getAttachments().isEmpty());
    }
  }

  @Test
  @DisplayName("Should throw SQLException when ResultSet access throws SQLException")
  void testMapRow_ThrowsSQLException() throws SQLException {
    when(mockResultSet.getString("tags")).thenThrow(new SQLException("DB error"));

    assertThrows(SQLException.class, () -> rowMapper.mapRow(mockResultSet, 1));
  }
}
