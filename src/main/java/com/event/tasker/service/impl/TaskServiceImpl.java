package com.event.tasker.service.impl;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.event.tasker.DAO.impl.TaskDaoImpl;
import com.event.tasker.model.Task;
import com.event.tasker.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

  private final TaskDaoImpl taskDao;

  public ArrayList<Task> getTasks() {
    try {
      return taskDao.getTasks();
    } catch (Exception e) {
      log.error("Error getting tasks", e);
      return null;
    }
  }
}
