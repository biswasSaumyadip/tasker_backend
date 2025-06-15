package com.event.tasker.DAO;

import java.util.ArrayList;
import java.util.Optional;

import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;

public interface TaskDao {
  ArrayList<Task> getTasks();

  String createTask(Task task);

  Optional<Task> getTask(String taskId);

  boolean softDeleteTaskById(String taskId);

  Optional<TaskDetail> getTaskDetail(String taskId);
}
