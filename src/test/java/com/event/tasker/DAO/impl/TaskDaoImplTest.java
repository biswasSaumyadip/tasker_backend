package com.event.tasker.DAO.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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
import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.rowMapper.TaskDetailRowMapper;
import com.event.tasker.rowMapper.TaskRowMapper;
import com.event.tasker.util.CSVToArrayConverter;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskDaoImpl")
class TaskDaoImplTest {

  private Task sampleTask;
  @Mock private NamedParameterJdbcTemplate jdbcTemplate;

  @InjectMocks private TaskDaoImpl taskDao;

  @BeforeEach
  void setUp() {
    sampleTask =
        Task.builder()
            .id(UUID.randomUUID().toString())
            .title("Test Task")
            .description("Test Description")
            .completed(false)
            .priority(Task.Priority.MEDIUM)
            .dueDate(Instant.now())
            .assignedTo("user-123")
            .parentId(null)
            .build();
  }

  @Test
  @DisplayName("Unit Test: Get tasks returns multiple tasks successfully")
  void testGetTasksReturnsMultipleTasks() {
    // Given
    Instant now = Instant.now();
    Timestamp timestamp = Timestamp.from(now);

    Task sampleTask1 =
        Task.builder()
            .id("1")
            .title("Task 1")
            .description("Description 1")
            .completed(false)
            .createdAt(timestamp.toInstant())
            .dueDate(timestamp.toInstant())
            .assignedTo("user1")
            .parentId(null)
            .tags(Arrays.asList("tag1", "tag2"))
            .priority(Task.Priority.HIGH)
            .profilePicture("profilePictureUrl")
            .build();

    Task sampleTask2 =
        Task.builder()
            .id("2")
            .title("Task 2")
            .description("Description 2")
            .completed(true)
            .createdAt(timestamp.toInstant())
            .dueDate(timestamp.toInstant())
            .assignedTo("user2")
            .parentId("1")
            .tags(List.of("tag3"))
            .priority(Task.Priority.LOW)
            .profilePicture(null)
            .build();
    List<Task> mockResult = Arrays.asList(sampleTask1, sampleTask2);

    when(jdbcTemplate.query(anyString(), any(TaskRowMapper.class))).thenReturn(mockResult);

    // When
    ArrayList<Task> tasks = taskDao.getTasks();

    // Then
    assertNotNull(tasks, "Tasks list should not be null");
    assertEquals(2, tasks.size(), "Should return 2 tasks");

    // Verify first task
    Task task1 = tasks.getFirst();
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
        "profilePictureUrl", task1.getProfilePicture(), "First task profile picture should match");

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
    assertEquals(List.of("tag3"), task2.getTags(), "Second task tags should match");
    assertNull(task2.getProfilePicture(), "Second task profile picture should be null");

    // Verify jdbcTemplate was called with correct SQL
    verify(jdbcTemplate).query(anyString(), any(TaskRowMapper.class));
  }

  @Test
  @DisplayName("Unit Test: Get tasks returns empty list when no tasks exist")
  void testGetTasksReturnsEmptyList() {
    // Given
    when(jdbcTemplate.query(anyString(), any(TaskRowMapper.class))).thenReturn(new ArrayList<>());

    // When
    ArrayList<Task> tasks = taskDao.getTasks();

    // Then
    assertNotNull(tasks, "Tasks list should not be null even when empty");
    assertTrue(tasks.isEmpty(), "Tasks list should be empty");

    verify(jdbcTemplate).query(anyString(), any(TaskRowMapper.class));
  }

  @Test
  @DisplayName("Unit Test: Get tasks throws SQLException when database error occurs")
  void testGetTasksThrowsSQLException() {
    // Given
    when(jdbcTemplate.query(anyString(), any(TaskRowMapper.class)))
        .thenThrow(new DataAccessException("Database error") {});

    // When/Then
    DataAccessException exception =
        assertThrows(
            DataAccessException.class,
            () -> taskDao.getTasks(),
            "Should throw SQLException when database error occurs");
    assertEquals("Database error", exception.getMessage(), "Exception message should match");

    // Verify jdbcTemplate was called with correct SQL
    verify(jdbcTemplate).query(anyString(), any(TaskRowMapper.class));
  }

  @Test
  @DisplayName("Unit Test: createTask should insert task and return ID")
  void testCreateTaskSuccess() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

    // Act
    String result = taskDao.createTask(sampleTask);

    // Assert
    assertNotNull(result, "Returned ID should not be null");
    assertEquals(sampleTask.getId(), result, "Returned ID should match input");
    verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  @DisplayName("Unit Test: createTask should return null if insert fails")
  void testCreateTaskReturnsNullWhenNotInserted() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class))).thenReturn(0);

    // Act
    String result = taskDao.createTask(sampleTask);

    // Assert
    assertNull(result, "Should return null when no rows are inserted");
    verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  @DisplayName("Unit Test: createTask should throw DataAccessException on DB error")
  void testCreateTaskThrowsDataAccessException() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
        .thenThrow(new DataAccessException("Simulated DB error") {});

    // Act & Assert
    assertThrows(DataAccessException.class, () -> taskDao.createTask(sampleTask));
    verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  @DisplayName("Unit Test: createTask should throw RuntimeException on unexpected error")
  void testCreateTaskThrowsRuntimeException() {
    // Arrange
    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
        .thenThrow(new RuntimeException("Unexpected error"));

    // Act & Assert
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> taskDao.createTask(sampleTask));

    assertTrue(exception.getMessage().contains("Unexpected error"));
    verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  @DisplayName("Unit Test: getTask should return a populated Task when found")
  void testGetTaskSuccess() {
    // Arrange
    String taskId = "task-123";
    Instant now = Instant.now();
    Timestamp timestamp = Timestamp.from(now);

    Task task =
        Task.builder()
            .id(taskId)
            .title("A Great Task")
            .assignedTo("John Doe")
            .createdAt(timestamp.toInstant())
            .dueDate(timestamp.toInstant())
            .profilePicture("https://example.com/pic.png")
            .parentId("parent-456")
            .tags(CSVToArrayConverter.convertCommaSeparated("backend,java", String::trim))
            .build();

    when(jdbcTemplate.queryForObject(
            anyString(), any(SqlParameterSource.class), any(TaskRowMapper.class)))
        .thenReturn(task);

    Optional<Task> optionalTask = taskDao.getTask(taskId);
    Task resultTask = optionalTask.orElse(null);

    // Assert
    assertNotNull(resultTask, "Task should not be null");
    assertEquals(taskId, resultTask.getId(), "Task ID should match");
    assertEquals("A Great Task", resultTask.getTitle(), "Title should match");
    assertEquals("John Doe", resultTask.getAssignedTo(), "Assignee should match");
    assertEquals(now, resultTask.getCreatedAt(), "Creation timestamp should match");
    assertEquals(now, resultTask.getDueDate(), "Due date should match");
    assertEquals(
        "https://example.com/pic.png",
        resultTask.getProfilePicture(),
        "Profile picture URL should match");
    assertEquals("parent-456", resultTask.getParentId(), "Parent ID should match");
    assertEquals(
        Arrays.asList("backend", "java"), resultTask.getTags(), "Tags should be correctly parsed");

    // Verify that the query method was called on the template
    verify(jdbcTemplate)
        .queryForObject(anyString(), any(SqlParameterSource.class), any(TaskRowMapper.class));
  }

  @Test
  @DisplayName("Unit Test: getTask should return an empty Task object if not found")
  void testGetTaskNotFound() {
    // Arrange
    String taskId = "non-existent-task";

    when(jdbcTemplate.queryForObject(
            anyString(), any(SqlParameterSource.class), any(TaskRowMapper.class)))
        .thenThrow(new EmptyResultDataAccessException(1));

    // Act
    Optional<Task> optionalTask = taskDao.getTask(taskId);

    // Assert
    assertTrue(optionalTask.isEmpty(), "Should return Optional.empty() when task not found");
  }

  // The exception test remains the same as it doesn't rely on the ResultSet
  @Test
  @DisplayName("Unit Test: getTask should throw DataAccessException on database error")
  void testGetTaskThrowsDataAccessException() {
    // Arrange
    String taskId = "any-id";
    when(jdbcTemplate.queryForObject(
            anyString(), any(SqlParameterSource.class), any(TaskRowMapper.class)))
        .thenThrow(new DataAccessException("Simulated DB error") {});

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> taskDao.getTask(taskId),
        "Should propagate DataAccessException from jdbcTemplate");
  }

  @Test
  @DisplayName("Unit Test: softDeleteTaskById should return true if task was updated")
  void testSoftDeleteTaskById_successfulDelete() {
    // Arrange
    String taskId = "123";
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

    // Act
    boolean result = taskDao.softDeleteTaskById(taskId);

    // Assert
    assertTrue(result, "Should return true when a row is updated");
  }

  @Test
  @DisplayName("Unit Test: softDeleteTaskById should return false if no row was updated")
  void testSoftDeleteTaskById_notFoundOrAlreadyDeleted() {
    // Arrange
    String taskId = "not-found";
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(0);

    // Act
    boolean result = taskDao.softDeleteTaskById(taskId);

    // Assert
    assertFalse(result, "Should return false when no row is updated");
  }

  @Test
  @DisplayName("Unit Test: softDeleteTaskById should throw exception on DB error")
  void testSoftDeleteTaskById_throwsException() {
    // Arrange
    String taskId = "error-case";
    DataAccessException dataAccessException = mock(DataAccessException.class);
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class)))
        .thenThrow(dataAccessException);

    // Act & Assert
    DataAccessException thrown =
        assertThrows(
            DataAccessException.class,
            () -> taskDao.softDeleteTaskById(taskId),
            "Should throw DataAccessException on DB error");

    assertEquals(dataAccessException, thrown, "Thrown exception should match the mock");
  }

  @Test
  @DisplayName("Should return task detail if found")
  void testGetTaskDetail_HappyPath() {
    // --- Arrange ---

    final String taskId = "task-123";

    TaskDetail mockTask =
        TaskDetail.builder()
            .id(taskId)
            .title("Test Task")
            .description("Description")
            .completed(false)
            .priority(Task.Priority.HIGH)
            .assignedTo("Alice Bob")
            .dueDate(Instant.now())
            .parentId("parent-1")
            .tags(List.of("java", "spring"))
            .attachments(List.of(Attachment.builder().fileName("file.pdf").build()))
            .build();

    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(TaskDetailRowMapper.class)))
        .thenReturn(mockTask);

    // --- Act ---
    Optional<TaskDetail> result = taskDao.getTaskDetail(taskId);

    // --- Assert ---
    assertTrue(result.isPresent());
    assertEquals(taskId, result.get().getId());
    assertEquals("Test Task", result.get().getTitle());
    assertEquals("Alice Bob", result.get().getAssignedTo());

    // Verify SQL executed with correct params
    ArgumentCaptor<MapSqlParameterSource> paramCaptor =
        ArgumentCaptor.forClass(MapSqlParameterSource.class);
    verify(jdbcTemplate)
        .queryForObject(anyString(), paramCaptor.capture(), any(TaskDetailRowMapper.class));
    assertEquals(taskId, paramCaptor.getValue().getValue("taskId"));
  }

  @Test
  @DisplayName("Should return empty optional when task not found")
  void testGetTaskDetail_NotFound() {
    // --- Arrange ---
    final String taskId = "task-123";
    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(TaskDetailRowMapper.class)))
        .thenThrow(new RuntimeException("No result"));

    // --- Act ---
    Optional<TaskDetail> result = taskDao.getTaskDetail(taskId);

    // --- Assert ---
    assertTrue(result.isEmpty(), "Expected result to be empty when exception occurs.");
    verify(jdbcTemplate)
        .queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(TaskDetailRowMapper.class));
  }

  @Test
  @DisplayName("Should handle null return from queryForObject safely")
  void testGetTaskDetail_NullReturn() {
    // --- Arrange ---
    final String taskId = "task-123";
    when(jdbcTemplate.queryForObject(
            anyString(), any(MapSqlParameterSource.class), any(TaskDetailRowMapper.class)))
        .thenReturn(null);

    // --- Act ---
    Optional<TaskDetail> result = taskDao.getTaskDetail(taskId);

    // --- Assert ---
    assertTrue(result.isEmpty());
  }
}
