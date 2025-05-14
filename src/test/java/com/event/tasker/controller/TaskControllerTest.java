package com.event.tasker.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

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
class TaskControllerTest {

  @Mock private TaskService taskService;

  @InjectMocks private TaskController taskController;

  @Test
  void testGetTasks_Success() {
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
  void testGetTasks_NullResponse() {
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
  void testGetTasks_Exception() {
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
  void testControllerSetup() {
    // Given
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

    // Then
    assertNotNull(mockMvc, "MockMvc should be initialized");
  }
}
