package com.event.tasker.rowMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.event.tasker.model.Task;
import com.event.tasker.util.CSVToArrayConverter;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskRowMapper")
class TaskRowMapperTest {

  @Mock private ResultSet mockResultSet;

  private TaskRowMapper taskRowMapper;

  @BeforeEach
  void setUp() {
    // Instantiate the class we are testing
    taskRowMapper = new TaskRowMapper();
  }

  @Test
  @DisplayName("Should map a ResultSet row to a Task object correctly")
  void testMapRow_HappyPath() throws SQLException {
    String taskId = UUID.randomUUID().toString();
    Instant now = Instant.now();
    Timestamp timestampNow = Timestamp.from(now);
    String tagsString = "java,spring,backend";

    when(mockResultSet.getString("id")).thenReturn(taskId);
    when(mockResultSet.getString("title")).thenReturn("Build a REST API");
    when(mockResultSet.getString("description")).thenReturn("Use Spring Boot to build the API");
    when(mockResultSet.getBoolean("completed")).thenReturn(false);
    when(mockResultSet.getString("priority")).thenReturn("HIGH");
    when(mockResultSet.getString("assignedTo")).thenReturn("John Doe");
    when(mockResultSet.getString("profilePicture")).thenReturn("http://example.com/pic.png");
    when(mockResultSet.getTimestamp("createdAt")).thenReturn(timestampNow);
    when(mockResultSet.getTimestamp("dueDate")).thenReturn(timestampNow);
    when(mockResultSet.getString("parentId")).thenReturn("parent-task-123");
    when(mockResultSet.getString("tags")).thenReturn(tagsString);

    try (MockedStatic<CSVToArrayConverter> mockedConverter =
        Mockito.mockStatic(CSVToArrayConverter.class)) {
      List<String> expectedTags = Arrays.asList("java", "spring", "backend");
      mockedConverter
          .when(() -> CSVToArrayConverter.convertCommaSeparated(eq(tagsString), any()))
          .thenReturn(expectedTags);

      // --- Act ---
      Task resultTask = taskRowMapper.mapRow(mockResultSet, 1);

      // --- Assert ---
      assertNotNull(resultTask, "The resulting task should not be null.");
      assertEquals(taskId, resultTask.getId());
      assertEquals("Build a REST API", resultTask.getTitle());
      assertEquals("Use Spring Boot to build the API", resultTask.getDescription());
      assertFalse(resultTask.isCompleted());
      assertEquals(Task.Priority.HIGH, resultTask.getPriority());
      assertEquals("John Doe", resultTask.getAssignedTo());
      assertEquals("http://example.com/pic.png", resultTask.getProfilePicture());
      assertEquals(now, resultTask.getCreatedAt());
      assertEquals(now, resultTask.getDueDate());
      assertEquals("parent-task-123", resultTask.getParentId());
      assertEquals(
          expectedTags, resultTask.getTags(), "Tags list should match the mocked conversion.");
    }
  }

  @Test
  @DisplayName("Should handle null tags by returning an empty list")
  void testMapRow_NullTags() throws SQLException {
    // --- Arrange ---
    String taskId = "task-no-tags";
    Instant now = Instant.now();
    Timestamp nowTimestamp = Timestamp.from(now);

    when(mockResultSet.getString("id")).thenReturn(taskId);
    when(mockResultSet.getString("title")).thenReturn("Task with null tags");
    when(mockResultSet.getString("description")).thenReturn("A description here");
    when(mockResultSet.getBoolean("completed")).thenReturn(true);
    when(mockResultSet.getString("priority")).thenReturn("LOW");
    when(mockResultSet.getString("assignedTo")).thenReturn("test-user");
    when(mockResultSet.getString("profilePicture")).thenReturn(null);
    when(mockResultSet.getTimestamp("createdAt")).thenReturn(nowTimestamp);
    when(mockResultSet.getTimestamp("dueDate")).thenReturn(nowTimestamp);
    when(mockResultSet.getString("parentId")).thenReturn(null);
    when(mockResultSet.getString("tags")).thenReturn(null);

    // --- Act ---
    Task resultTask = taskRowMapper.mapRow(mockResultSet, 1);

    // --- Assert ---
    assertNotNull(resultTask);
    assertEquals(taskId, resultTask.getId());
    assertEquals("Task with null tags", resultTask.getTitle());
    assertEquals("A description here", resultTask.getDescription());
    assertTrue(resultTask.isCompleted());
    assertEquals(Task.Priority.LOW, resultTask.getPriority());
    assertEquals("test-user", resultTask.getAssignedTo());
    assertNull(resultTask.getProfilePicture());
    assertEquals(now, resultTask.getCreatedAt());
    assertEquals(now, resultTask.getDueDate());
    assertNull(resultTask.getParentId());
    assertNotNull(resultTask.getTags());
    assertTrue(resultTask.getTags().isEmpty(), "Tags list should be empty when input is null.");
  }

  @Test
  @DisplayName("Should throw SQLException when a database error occurs")
  void testMapRow_ThrowsSQLException() throws SQLException {
    // --- Arrange ---
    // Simulate failure at the very first interaction
    when(mockResultSet.getString("tags")).thenReturn("test,implementation");
    when(mockResultSet.getString("id")).thenReturn("some-id");
    when(mockResultSet.getString("title")).thenReturn("some-title");
    when(mockResultSet.getString("description")).thenReturn("desc");
    when(mockResultSet.getBoolean("completed")).thenReturn(false);
    when(mockResultSet.getString("priority")).thenReturn("HIGH");
    when(mockResultSet.getString("assignedTo")).thenReturn("assignee");
    when(mockResultSet.getString("profilePicture")).thenReturn("pic.png");
    when(mockResultSet.getTimestamp("createdAt")).thenThrow(new SQLException("Fail at timestamp"));

    // --- Act & Assert ---
    SQLException exception =
        assertThrows(
            SQLException.class,
            () -> {
              taskRowMapper.mapRow(mockResultSet, 1);
            });

    assertEquals("Fail at timestamp", exception.getMessage());
  }
}
