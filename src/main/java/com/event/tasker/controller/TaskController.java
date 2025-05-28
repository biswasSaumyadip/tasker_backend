package com.event.tasker.controller;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.model.TaskerResponse;
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
  public ResponseEntity<TaskerResponse<ArrayList<Task>>> getTasks() {
    try {
      ArrayList<Task> tasks = taskService.getTasks();
      if (tasks == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }

      TaskerResponse<ArrayList<Task>> response =
          TaskerResponse.<ArrayList<Task>>builder().data(tasks).build();

      return ResponseEntity.ok(response);
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
  public Object createTask(@RequestBody TaskDetail task) {
    return new Object() {};
  }

  @GetMapping("/{id}/start")
  public Object startTask(String id) {
    return new Object() {};
  }
}
