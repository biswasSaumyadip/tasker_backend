package com.event.tasker.service;

import java.util.ArrayList;

import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.model.TaskerResponse;

public interface TaskService {
  ArrayList<Task> getTasks();

  TaskerResponse<String> addTask(TaskDetail task);
}
