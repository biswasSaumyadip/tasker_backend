package com.event.tasker.service.impl;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.event.tasker.DAO.TaskAttachmentDao;
import com.event.tasker.DAO.TaskTagDao;
import com.event.tasker.DAO.impl.TaskDaoImpl;
import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.model.TaskTag;
import com.event.tasker.model.TaskerResponse;
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

  public ArrayList<Task> getTasks() {
    try {
      return taskDao.getTasks();
    } catch (Exception e) {
      log.error("Error getting tasks", e);
      return null;
    }
  }

  @Override
  @Transactional
  public TaskerResponse<String> addTask(TaskDetail task) {
    log.info("Adding task {}", task.getId());

    // First file gets upload
    // in case if upload fails, user should get message of failure
    // if failed then gets prompt whether to proceed and upload later or try again or cancel task
    // creation
    //

    TaskerResponse<String> response = new TaskerResponse<>();

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

    log.info("Task {} added", task.getId());

    log.info("Task tags {}", task.getTags());

    ArrayList<TaskTag> taskTags = new ArrayList<>();
    for (String tag : task.getTags()) {
      taskTags.add(TaskTag.builder().taskId(task.getId()).name(tag).build());
    }

    log.info("{} tags are ready to be inserted", taskTags.size());
    int insertedRowCount = taskTagDao.createTaskTags(taskTags);

    if (insertedRowCount > 0) {
      log.info("Task tags inserted successfully");
    } else if (insertedRowCount != taskTags.size()) {
      log.warn("{} task tags insert failed", taskTags.size() - insertedRowCount);
    } else {
      log.error("Task tags insert failed");
    }

    //    taskAttachmentDao.createAttachment();

    return null;
  }
}
