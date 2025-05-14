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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.event.tasker.model.Task;
import com.event.tasker.util.CSVToArrayConverter;

@ExtendWith(MockitoExtension.class)
class TaskDaoImplTest {

  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private TaskDaoImpl taskDao;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(taskDao, "jdbcTemplate", jdbcTemplate);
  }

  @Test
  void testGetTasks() throws SQLException {
    // Given
    String expectedSql =
        "\tSELECT t.*, GROUP_CONCAT(tt.tag) AS tags\n"
            + "\tFROM TASKS t\n"
            + "\tLEFT JOIN TASK_TAGS tt ON t.id = tt.task_id\n"
            + "\tGROUP BY t.id";

    ResultSet mockResultSet = mock(ResultSet.class);

    // Mock result set behavior
    when(mockResultSet.next()).thenReturn(true, true, false); // Two rows

    // First row
    when(mockResultSet.getString("id")).thenReturn("1", "2");
    when(mockResultSet.getString("title")).thenReturn("Task 1", "Task 2");
    when(mockResultSet.getString("description")).thenReturn("Description 1", "Description 2");
    when(mockResultSet.getBoolean("completed")).thenReturn(false, true);

    Instant now = Instant.now();
    Timestamp timestamp = Timestamp.from(now);
    when(mockResultSet.getTimestamp("createdAt")).thenReturn(timestamp, timestamp);
    when(mockResultSet.getTimestamp("dueDate")).thenReturn(timestamp, timestamp);

    when(mockResultSet.getString("assignedTo")).thenReturn("user1", "user2");
    when(mockResultSet.getString("parentId")).thenReturn(null, "1");
    when(mockResultSet.getString("tags")).thenReturn("tag1,tag2", "tag3");

    // Mock jdbcTemplate behavior
    when(jdbcTemplate.query(eq(expectedSql), any(ResultSetExtractor.class)))
        .thenAnswer(
            invocation -> {
              ResultSetExtractor<ArrayList<Task>> extractor = invocation.getArgument(1);
              return extractor.extractData(mockResultSet);
            });

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
      assertNotNull(tasks);
      assertEquals(2, tasks.size());

      // Verify first task
      Task task1 = tasks.get(0);
      assertEquals("1", task1.getId());
      assertEquals("Task 1", task1.getTitle());
      assertEquals("Description 1", task1.getDescription());
      assertFalse(task1.isCompleted());
      assertEquals(now, task1.getCreatedAt());
      assertEquals(now, task1.getDueDate());
      assertEquals("user1", task1.getAssignedTo());
      assertNull(task1.getParentId());
      assertEquals(Arrays.asList("tag1", "tag2"), task1.getTags());

      // Verify second task
      Task task2 = tasks.get(1);
      assertEquals("2", task2.getId());
      assertEquals("Task 2", task2.getTitle());
      assertEquals("Description 2", task2.getDescription());
      assertTrue(task2.isCompleted());
      assertEquals(now, task2.getCreatedAt());
      assertEquals(now, task2.getDueDate());
      assertEquals("user2", task2.getAssignedTo());
      assertEquals("1", task2.getParentId());
      assertEquals(Arrays.asList("tag3"), task2.getTags());

      // Verify jdbcTemplate was called with correct SQL
      verify(jdbcTemplate).query(eq(expectedSql), any(ResultSetExtractor.class));
    }
  }

  @Test
  void testGetTasksWithNoResults() throws SQLException {
    // Given
    String expectedSql =
        "\tSELECT t.*, GROUP_CONCAT(tt.tag) AS tags\n"
            + "\tFROM TASKS t\n"
            + "\tLEFT JOIN TASK_TAGS tt ON t.id = tt.task_id\n"
            + "\tGROUP BY t.id";

    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.next()).thenReturn(false); // No rows

    // Mock jdbcTemplate behavior
    when(jdbcTemplate.query(eq(expectedSql), any(ResultSetExtractor.class)))
        .thenAnswer(
            invocation -> {
              ResultSetExtractor<ArrayList<Task>> extractor = invocation.getArgument(1);
              return extractor.extractData(mockResultSet);
            });

    // When
    ArrayList<Task> tasks = taskDao.getTasks();

    // Then
    assertNotNull(tasks);
    assertTrue(tasks.isEmpty());

    // Verify jdbcTemplate was called with correct SQL
    verify(jdbcTemplate).query(eq(expectedSql), any(ResultSetExtractor.class));
  }

  @Test
  void testGetTasksWithSQLException() throws SQLException {
    // Given
    String expectedSql =
        "\tSELECT t.*, GROUP_CONCAT(tt.tag) AS tags\n"
            + "\tFROM TASKS t\n"
            + "\tLEFT JOIN TASK_TAGS tt ON t.id = tt.task_id\n"
            + "\tGROUP BY t.id";

    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.next()).thenThrow(new SQLException("Database error"));

    // Mock jdbcTemplate behavior
    when(jdbcTemplate.query(eq(expectedSql), any(ResultSetExtractor.class)))
        .thenAnswer(
            invocation -> {
              ResultSetExtractor<ArrayList<Task>> extractor = invocation.getArgument(1);
              return extractor.extractData(mockResultSet);
            });

    // When/Then
    assertThrows(SQLException.class, () -> taskDao.getTasks());

    // Verify jdbcTemplate was called with correct SQL
    verify(jdbcTemplate).query(eq(expectedSql), any(ResultSetExtractor.class));
  }
}
