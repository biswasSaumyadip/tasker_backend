package com.event.tasker.DAO.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.event.tasker.model.Task;
import com.event.tasker.util.CSVToArrayConverter;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskDaoImpl")
class TaskDaoImplTest {

  private static final String EXPECTED_SQL =
      "SELECT t.*,\n"
          + "       CONCAT(u.first_name, ' ', u.last_name) AS assignedTo,\n"
          + "       t.created_at                           AS createdAt,\n"
          + "       t.due_date                             AS dueDate,\n"
          + "       u.profile_picture_url                  AS profilePicture, \n"
          + "       parent_id                              AS parentID,\n"
          + "       GROUP_CONCAT(tt.tag)                   AS tags\n"
          + "FROM tasks t\n"
          + "     LEFT JOIN task_tags tt ON t.id = tt.task_id\n"
          + "     LEFT JOIN users u ON u.user_id = t.assigned_to\n"
          + "GROUP BY t.id, u.first_name, u.last_name";

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private TaskDaoImpl taskDao;

  @Test
  @DisplayName("Get tasks returns multiple tasks successfully")
  void testGetTasks_ReturnsMultipleTasks() throws SQLException {
    // Given
    ResultSet mockResultSet = mock(ResultSet.class);
    Instant now = Instant.now();
    Timestamp timestamp = Timestamp.from(now);

    // Mock result set behavior for two rows
    when(mockResultSet.next()).thenReturn(true, true, false);

    // Task 1 data
    when(mockResultSet.getString("id")).thenReturn("1", "2");
    when(mockResultSet.getString("title")).thenReturn("Task 1", "Task 2");
    when(mockResultSet.getString("description")).thenReturn("Description 1", "Description 2");
    when(mockResultSet.getBoolean("completed")).thenReturn(false, true);
    when(mockResultSet.getTimestamp("createdAt")).thenReturn(timestamp, timestamp);
    when(mockResultSet.getTimestamp("dueDate")).thenReturn(timestamp, timestamp);
    when(mockResultSet.getString("assignedTo")).thenReturn("user1", "user2");
    when(mockResultSet.getString("parentId")).thenReturn(null, "1");
    when(mockResultSet.getString("tags")).thenReturn("tag1,tag2", "tag3");
    when(mockResultSet.getString("priority")).thenReturn("HIGH", "LOW");
    when(mockResultSet.getString("profilePicture")).thenReturn(null, "profilePictureUrl");

    // Mock jdbcTemplate behavior
    mockJdbcTemplateQuery(mockResultSet);

    // Mock CSVToArrayConverter
    try (MockedStatic<CSVToArrayConverter> mockedConverter =
        Mockito.mockStatic(CSVToArrayConverter.class)) {
      mockedConverter
          .when(() -> CSVToArrayConverter.convertCommaSeparated(eq("tag1,tag2"), any()))
          .thenReturn(Arrays.asList("tag1", "tag2"));
      mockedConverter
          .when(() -> CSVToArrayConverter.convertCommaSeparated(eq("tag3"), any()))
          .thenReturn(Arrays.asList("tag3"));

      // When
      ArrayList<Task> tasks = taskDao.getTasks();

      // Then
      assertNotNull(tasks, "Tasks list should not be null");
      assertEquals(2, tasks.size(), "Should return 2 tasks");

      // Verify first task
      Task task1 = tasks.get(0);
      assertEquals("1", task1.getId(), "First task ID should match");
      assertEquals("Task 1", task1.getTitle(), "First task title should match");
      assertEquals("Description 1", task1.getDescription(), "First task description should match");
      assertFalse(task1.isCompleted(), "First task should not be completed");
      assertEquals(now, task1.getCreatedAt(), "First task creation time should match");
      assertEquals(now, task1.getDueDate(), "First task due date should match");
      assertEquals("user1", task1.getAssignedTo(), "First task assignee should match");
      assertNull(task1.getParentId(), "First task should not have a parent");
      assertEquals(Task.Priority.HIGH, task1.getPriority(), "First task priority should be HIGH");
      assertEquals(Arrays.asList("tag1", "tag2"), task1.getTags(), "First task tags should match");
      assertEquals(
          "profilePictureUrl",
          task1.getProfilePicture(),
          "First task profile picture should match");

      // Verify second task
      Task task2 = tasks.get(1);
      assertEquals("2", task2.getId(), "Second task ID should match");
      assertEquals("Task 2", task2.getTitle(), "Second task title should match");
      assertEquals("Description 2", task2.getDescription(), "Second task description should match");
      assertTrue(task2.isCompleted(), "Second task should be completed");
      assertEquals(now, task2.getCreatedAt(), "Second task creation time should match");
      assertEquals(now, task2.getDueDate(), "Second task due date should match");
      assertEquals("user2", task2.getAssignedTo(), "Second task assignee should match");
      assertEquals("1", task2.getParentId(), "Second task should have parent ID 1");
      assertEquals(Task.Priority.LOW, task2.getPriority(), "Second task priority should be LOW");
      assertEquals(Arrays.asList("tag3"), task2.getTags(), "Second task tags should match");
      assertNull(task2.getProfilePicture(), "Second task profile picture should be null");

      // Verify jdbcTemplate was called with correct SQL
      verify(jdbcTemplate).query(eq(EXPECTED_SQL), any(ResultSetExtractor.class));
    }
  }

  @Test
  @DisplayName("Get tasks returns empty list when no tasks exist")
  void testGetTasks_ReturnsEmptyList() throws SQLException {
    // Given
    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.next()).thenReturn(false); // No rows

    // Mock jdbcTemplate behavior
    mockJdbcTemplateQuery(mockResultSet);

    // When
    ArrayList<Task> tasks = taskDao.getTasks();

    // Then
    assertNotNull(tasks, "Tasks list should not be null even when empty");
    assertTrue(tasks.isEmpty(), "Tasks list should be empty");

    // Verify jdbcTemplate was called with correct SQL
    verify(jdbcTemplate).query(eq(EXPECTED_SQL), any(ResultSetExtractor.class));
  }

  @Test
  @DisplayName("Get tasks throws SQLException when database error occurs")
  void testGetTasks_ThrowsSQLException() throws SQLException {
    // Given
    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.next()).thenThrow(new SQLException("Database error"));

    // Mock jdbcTemplate behavior
    mockJdbcTemplateQuery(mockResultSet);

    // When/Then
    SQLException exception =
        assertThrows(
            SQLException.class,
            () -> taskDao.getTasks(),
            "Should throw SQLException when database error occurs");
    assertEquals("Database error", exception.getMessage(), "Exception message should match");

    // Verify jdbcTemplate was called with correct SQL
    verify(jdbcTemplate).query(eq(EXPECTED_SQL), any(ResultSetExtractor.class));
  }

  /** Helper method to set up common jdbcTemplate mocking behavior */
  private void mockJdbcTemplateQuery(ResultSet mockResultSet) {
    when(jdbcTemplate.query(eq(EXPECTED_SQL), any(ResultSetExtractor.class)))
        .thenAnswer(
            invocation -> {
              ResultSetExtractor<ArrayList<Task>> extractor = invocation.getArgument(1);
              return extractor.extractData(mockResultSet);
            });
  }
}
