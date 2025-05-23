package com.event.tasker.controller;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.event.tasker.model.Task;
import com.event.tasker.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

  private final TaskService taskService;

  @GetMapping("/list")
  public ResponseEntity<ArrayList<Task>> getTasks() {
    try {
      ArrayList<Task> tasks = taskService.getTasks();
      if (tasks == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
      return ResponseEntity.ok(tasks);
    } catch (Exception e) {
      log.error("Error retrieving tasks", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{id}")
  public Object getTaskById(String id) {
    return new Object() {
      String test = "test";
    };
  }

  @PostMapping()
  public Object createTask(@RequestBody Task task) {
    return new Object() {};
  }

  @GetMapping("/{id}/start")
  public Object startTask(String id) {
    return new Object() {};
  }
}
