package com.event.tasker.DAO;

import java.util.ArrayList;

import com.event.tasker.model.TaskTag;

public interface TaskTagDao {
  int createTaskTags(ArrayList<TaskTag> tags);
}
