package com.event.tasker.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.event.tasker.model.Task;
import com.event.tasker.service.TaskService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskController")
class TaskControllerTest {

  @Mock private TaskService taskService;

  @InjectMocks private TaskController taskController;

  @Test
  @DisplayName("Get tasks returns successful response with task list")
  void testGetTasksSuccess() {
    // Given
    ArrayList<Task> mockTasks = new ArrayList<>();
    mockTasks.add(new Task());
    when(taskService.getTasks()).thenReturn(mockTasks);

    // When
    ResponseEntity<ArrayList<Task>> response = taskController.getTasks();

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode(), "Should return OK status");
    assertEquals(mockTasks, response.getBody(), "Should return tasks from service");
  }

  @Test
  @DisplayName("Get tasks returns error when service returns null")
  void testGetTasksNullResponse() {
    // Given
    when(taskService.getTasks()).thenReturn(null);

    // When
    ResponseEntity<ArrayList<Task>> response = taskController.getTasks();

    // Then
    assertEquals(
        HttpStatus.INTERNAL_SERVER_ERROR,
        response.getStatusCode(),
        "Should return INTERNAL_SERVER_ERROR when service returns null");
  }

  @Test
  @DisplayName("Get tasks returns error when service throws exception")
  void testGetTasksException() {
    // Given
    when(taskService.getTasks()).thenThrow(new RuntimeException("Test exception"));

    // When
    ResponseEntity<ArrayList<Task>> response = taskController.getTasks();

    // Then
    assertEquals(
        HttpStatus.INTERNAL_SERVER_ERROR,
        response.getStatusCode(),
        "Should return INTERNAL_SERVER_ERROR when service throws exception");
  }

  @Test
  @DisplayName("Controller setup initializes MockMvc correctly")
  void testControllerSetup() {
    // Given
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

    // Then
    assertNotNull(mockMvc, "MockMvc should be initialized");
  }
}
