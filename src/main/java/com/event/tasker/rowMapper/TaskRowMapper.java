package com.event.tasker.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.event.tasker.model.Task;
import com.event.tasker.util.CSVToArrayConverter;

public class TaskRowMapper implements RowMapper<Task> {
  @Override
  public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
    String tagsString = rs.getString("tags");
    List<String> tags =
        (tagsString != null)
            ? CSVToArrayConverter.convertCommaSeparated(tagsString, String::trim)
            : Collections.emptyList();

    return Task.builder()
        .id(rs.getString("id"))
        .title(rs.getString("title"))
        .description(rs.getString("description"))
        .completed(rs.getBoolean("completed"))
        .priority(Task.Priority.fromCode(rs.getInt("priority")))
        .assignedTo(rs.getString("assignedTo"))
        .profilePicture(rs.getString("profilePicture"))
        .createdAt(rs.getTimestamp("createdAt").toInstant())
        .dueDate(rs.getTimestamp("dueDate").toInstant())
        .parentId(rs.getString("parentId"))
        .tags(tags)
        .build();
  }
}
