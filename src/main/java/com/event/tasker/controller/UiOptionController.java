package com.event.tasker.controller;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.event.tasker.model.TaskerResponse;
import com.event.tasker.model.UiOption;
import com.event.tasker.service.UiOptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/uiConfig")
@Slf4j
@RequiredArgsConstructor
public class UiOptionController {
  private final UiOptionService uiOptionService;

  @GetMapping("/priorityLabels")
  public ResponseEntity<TaskerResponse<ArrayList<UiOption>>> getPriorityLabels(
      @RequestParam String id) {
    ArrayList<UiOption> priorityLabels = uiOptionService.getPriorityLabels(id);

    TaskerResponse<ArrayList<UiOption>> response =
        TaskerResponse.<ArrayList<UiOption>>builder().data(priorityLabels).build();

    return ResponseEntity.ok(response);
  }
}
