package com.event.tasker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.event.tasker.DAO.impl.TaskDaoImpl;
import com.event.tasker.model.Task;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskServiceImpl")
class TaskServiceImplTest {

  @Mock private TaskDaoImpl taskDao;

  @Mock private Logger logger;

  @InjectMocks private TaskServiceImpl taskService;

  @Test
  @DisplayName("Get tasks returns list of tasks successfully")
  void testGetTasksSuccess() {
    // Given
    ArrayList<Task> expectedTasks = new ArrayList<>();
    Task task1 =
        Task.builder()
            .id("1")
            .title("Task 1")
            .description("Description 1")
            .completed(false)
            .priority(Task.Priority.LOW)
            .dueDate(Instant.now())
            .createdAt(Instant.now())
            .assignedTo("user1")
            .parentId(null)
            .tags(Arrays.asList("tag1", "tag2"))
            .build();
    expectedTasks.add(task1);

    when(taskDao.getTasks()).thenReturn(expectedTasks);

    // When
    ArrayList<Task> actualTasks = taskService.getTasks();

    // Then
    assertNotNull(actualTasks);
    assertEquals(expectedTasks, actualTasks);
    verify(taskDao).getTasks();
    verifyNoInteractions(logger);
  }

  @Test
  @DisplayName("Get tasks returns empty list when no tasks exist")
  void testGetTasksWithEmptyList() {
    // Given
    ArrayList<Task> expectedTasks = new ArrayList<>();
    when(taskDao.getTasks()).thenReturn(expectedTasks);

    // When
    ArrayList<Task> actualTasks = taskService.getTasks();

    // Then
    assertNotNull(actualTasks);
    assertTrue(actualTasks.isEmpty());
    verify(taskDao).getTasks();
    verifyNoInteractions(logger);
  }

  @Test
  @DisplayName("Get tasks returns null when database error occurs")
  void testGetTasksWithException() {
    // Given
    RuntimeException expectedException = new RuntimeException("Database error");
    when(taskDao.getTasks()).thenThrow(expectedException);

    // When
    ArrayList<Task> actualTasks = taskService.getTasks();

    // Then
    assertNull(actualTasks);
    verify(taskDao).getTasks();
    // Note: We can't easily verify the logging with Lombok's @Slf4j
    // as it creates a private static final logger
  }
}
