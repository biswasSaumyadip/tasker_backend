package com.event.tasker.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<TaskerResponse<String>> createTask(
      @RequestPart TaskDetail task,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    return ResponseEntity.ok(taskService.addTask(task, files));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<TaskerResponse<String>> deleteTask(@PathVariable String id) {
    return ResponseEntity.ok(taskService.deleteTask(id));
  }
}
