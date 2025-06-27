package com.event.tasker.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.event.tasker.DAO.TaskAttachmentDao;
import com.event.tasker.DAO.TaskTagDao;
import com.event.tasker.DAO.impl.TaskDaoImpl;
import com.event.tasker.model.Attachment;
import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.model.TaskTag;
import com.event.tasker.model.TaskerResponse;
import com.event.tasker.service.FileStorageService;
import com.event.tasker.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

  private final TaskDaoImpl taskDao;
  private final TaskAttachmentDao taskAttachmentDao;
  private final TaskTagDao taskTagDao;
  private final FileStorageService fileStorageService;
  private final TransactionTemplate transactionTemplate;

  public ArrayList<Task> getTasks() {
    try {
      return taskDao.getTasks();
    } catch (Exception e) {
      log.error("Error getting tasks", e);
      return null;
    }
  }

  @Override
  public TaskerResponse<String> addTask(TaskDetail task, List<MultipartFile> files) {
    task.setId(UUID.randomUUID().toString());
    log.info("Adding task {}", task.getId());

    ArrayList<TaskTag> taskTags = new ArrayList<>();
    for (String tag : task.getTags()) {
      taskTags.add(TaskTag.builder().taskId(task.getId()).name(tag).build());
    }

    String taskStatus =
        transactionTemplate.execute(
            status -> {
              String insertStatus =
                  taskDao.createTask(
                      Task.builder()
                          .id(task.getId())
                          .description(task.getDescription())
                          .title(task.getTitle())
                          .dueDate(task.getDueDate())
                          .priority(task.getPriority())
                          .parentId(task.getParentId())
                          .assignedTo(task.getAssignedTo())
                          .tags(task.getTags())
                          .build());

              taskTagDao.createTaskTags(taskTags);
              log.info("{} tags are ready to be inserted", taskTags.size());

              return insertStatus;
            });

    if (task.getAttachments() != null) {
      try {
        this.addAttachments(task.getId(), files);
      } catch (RuntimeException e) {
        // The file upload failed, but the task is already saved.
        // We must return a special response to tell the user.
        log.warn("Task {} created, but file upload failed.", task.getId());
        TaskerResponse<String> response = new TaskerResponse<>();
        response.setStatus("PARTIAL_SUCCESS");
        response.setMessage("Task created, but file attachment failed.");
        response.setData(taskStatus);
        return response;
      }
    }

    return TaskerResponse.<String>builder().message("Task created").status(taskStatus).build();
  }

  public void addAttachments(String taskId, List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      return;
    }

    for (MultipartFile file : files) {
      if (file.isEmpty()) {
        continue;
      }

      String uniqueFileName;
      try {
        uniqueFileName = fileStorageService.uploadFile(file);
        Attachment attachment = fileStorageService.getFileMetadata(uniqueFileName);
        attachment.setTaskId(taskId);
        taskAttachmentDao.createAttachment(attachment);
        // TODO: if db insertion fails then rollback file upload in background
      } catch (IOException e) {
        log.error(
            "Upload failed for one of the files for task {}. Rolling back this batch.", taskId, e);
        throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
      }
    }
  }

  @Override
  public TaskerResponse<String> deleteTask(String taskId) {
    try {
      if (taskDao.softDeleteTaskById(taskId)) {
        return TaskerResponse.<String>builder().message("Task deleted").status("DELETED").build();
      } else {
        return TaskerResponse.<String>builder().message("Task not found").build();
      }
    } catch (Exception e) {
      log.error("Error deleting task {}", taskId, e);
      throw e;
    }
  }

  @Override
  public TaskerResponse<TaskDetail> getTaskBy(String taskId) {
    try {
      Optional<TaskDetail> taskDetail = taskDao.getTaskDetail(taskId);

      if (taskDetail.isPresent()) {
        TaskDetail detail = taskDetail.get();
        return TaskerResponse.<TaskDetail>builder()
            .data(detail)
            .message("Task " + taskId + " found")
            .build();
      }
    } catch (Exception e) {
      log.error("Error getting task {}", taskId, e);
      throw e;
    }
    return null;
  }
}
