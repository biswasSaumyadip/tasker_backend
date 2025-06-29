package com.event.tasker.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDetail {
  private String id, title, description, assignedTo, parentId;

  @Builder.Default private boolean completed = false;

  @Builder.Default private Task.Priority priority = Task.Priority.LOW;

  private Instant dueDate;
  private List<String> tags;
  private String assignedToName;

  @Builder.Default private List<Attachment> attachments = new ArrayList<>();
  private List<String> teamMembers;
}
