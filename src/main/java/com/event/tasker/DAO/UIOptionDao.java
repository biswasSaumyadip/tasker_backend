package com.event.tasker.DAO;

import java.util.ArrayList;

import com.event.tasker.model.UiOption;

public interface UIOptionDao {
  ArrayList<UiOption> getPriorityLabels(String id);
}
