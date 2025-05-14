package com.event.tasker.DAO.impl;

import java.util.ArrayList;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TaskDao;
import com.event.tasker.model.Task;
import com.event.tasker.util.CSVToArrayConverter;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TaskDaoImpl implements TaskDao {

  private NamedParameterJdbcTemplate jdbcTemplate;

  public ArrayList<Task> getTasks() {
    String sql =
        "\tSELECT t.*, GROUP_CONCAT(tt.tag) AS tags\n"
            + "\tFROM TASKS t\n"
            + "\tLEFT JOIN TASK_TAGS tt ON t.id = tt.task_id\n"
            + "\tGROUP BY t.id";

    return jdbcTemplate.query(
        sql,
        resultSet -> {
          ArrayList<Task> tasks = new ArrayList<>();
          while (resultSet.next()) {
            // todo handle tags
            Task task =
                Task.builder()
                    .id(resultSet.getString("id"))
                    .assignedTo(resultSet.getString("assignedTo"))
                    .title(resultSet.getString("title"))
                    .createdAt(resultSet.getTimestamp("createdAt").toInstant())
                    .dueDate(resultSet.getTimestamp("dueDate").toInstant())
                    .description(resultSet.getString("description"))
                    .completed(resultSet.getBoolean("completed"))
                    .parentId(resultSet.getString("parentId"))
                    .tags(
                        CSVToArrayConverter.convertCommaSeparated(
                            resultSet.getString("tags"), String::trim))
                    .build();

            tasks.add(task);
          }

          return tasks;
        });
  }
}
