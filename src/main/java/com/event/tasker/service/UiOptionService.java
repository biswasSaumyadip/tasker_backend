package com.event.tasker.service;

import java.util.ArrayList;

import com.event.tasker.model.UiOption;

public interface UiOptionService {
  ArrayList<UiOption> getPriorityLabels(String id);
}
