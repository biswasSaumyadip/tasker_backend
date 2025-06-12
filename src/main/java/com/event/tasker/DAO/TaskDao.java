package com.event.tasker.DAO;

import java.util.ArrayList;

import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;

public interface TaskDao {
  ArrayList<Task> getTasks();

  String createTask(Task task);

  Task getTask(String taskId);

  boolean deleteTaskById(String taskId);

  TaskDetail getTaskDetail(String taskId);
}
