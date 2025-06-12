package com.event.tasker.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.model.TaskerResponse;

public interface TaskService {
  ArrayList<Task> getTasks();

  TaskerResponse<String> addTask(TaskDetail task, List<MultipartFile> files);

  void addAttachments(String taskId, List<MultipartFile> files);

  TaskerResponse<String> deleteTask(String taskId);

  TaskerResponse<String> getTaskBy(String taskId);
}
