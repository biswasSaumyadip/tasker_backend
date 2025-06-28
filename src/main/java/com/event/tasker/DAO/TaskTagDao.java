package com.event.tasker.DAO;

import java.util.ArrayList;

import com.event.tasker.model.TaskTag;

public interface TaskTagDao {
  int createTaskTags(ArrayList<TaskTag> tags);

  int deleteTaskTags(ArrayList<TaskTag> tags, String taskId);

  ArrayList<String> getTaskTagsBy(String uuid);
}
