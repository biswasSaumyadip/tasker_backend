package com.event.tasker.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event.tasker.model.Task;
import com.event.tasker.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController("/task")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  @GetMapping("/list")
  public ArrayList<Task> getTasks() {
    try {
      return taskService.getTasks();
    } catch (Exception e) {
      return null;
    }
  }
}
