package com.event.tasker.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.event.tasker.model.Attachment;
import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.util.CSVToArrayConverter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TaskDetailRowMapper implements RowMapper<TaskDetail> {
  private final Gson gson;

  @Override
  public TaskDetail mapRow(ResultSet rs, int rowNum) throws SQLException {

    String tagsString = rs.getString("tags");
    List<String> tags =
        (tagsString != null)
            ? CSVToArrayConverter.convertCommaSeparated(tagsString, String::trim)
            : Collections.emptyList();

    List<Attachment> attachments = new ArrayList<>();
    String attachmentString = rs.getString("attachments");
    if (attachmentString != null && !attachmentString.isEmpty() && !attachmentString.equals("[]")) {
      try {
        JsonArray attachmentJson = gson.fromJson(attachmentString, JsonArray.class);
        if (attachmentJson != null) {
          for (JsonElement jsonElement : attachmentJson) {
            attachments.add(gson.fromJson(jsonElement, Attachment.class));
          }
        }
      } catch (JsonSyntaxException e) {
        // It's better to log this error than to let it crash the entire request.
        log.error(
            "Failed to parse attachments JSON for task {}: {}", rs.getString("id"), e.getMessage());
      }
    }

    return TaskDetail.builder()
        .id(rs.getString("id"))
        .title(rs.getString("title"))
        .description(rs.getString("description"))
        .completed(rs.getBoolean("completed"))
        .priority(Task.Priority.valueOf(rs.getString("priority")))
        .assignedTo(rs.getString("assignedTo"))
        .dueDate(rs.getTimestamp("dueDate").toInstant())
        .parentId(rs.getString("parentId"))
        .tags(tags)
        .attachments(attachments)
        .build();
  }
}
