package com.event.tasker.DAO;

import java.util.ArrayList;

import com.event.tasker.model.Task;

public interface TaskDao {
  ArrayList<Task> getTasks();
}
