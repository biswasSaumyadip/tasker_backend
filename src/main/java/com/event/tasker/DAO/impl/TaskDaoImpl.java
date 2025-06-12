package com.event.tasker.DAO.impl;

import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TaskDao;
import com.event.tasker.model.Task;
import com.event.tasker.util.CSVToArrayConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TaskDaoImpl implements TaskDao {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public ArrayList<Task> getTasks() {
    String sql =
        "SELECT t.*,\n"
            + "       CONCAT(u.first_name, ' ', u.last_name) AS assignedTo,\n"
            + "       t.created_at                           AS createdAt,\n"
            + "       t.due_date                             AS dueDate,\n"
            + "       u.profile_picture_url                  AS profilePicture, \n"
            + "       parent_id                              AS parentID,\n"
            + "       GROUP_CONCAT(tt.tag)                   AS tags\n"
            + "FROM tasks t\n"
            + "     LEFT JOIN task_tags tt ON t.id = tt.task_id\n"
            + "     LEFT JOIN users u ON u.user_id = t.assigned_to\n"
            + "GROUP BY t.id, u.first_name, u.last_name";

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
                    .priority(Task.Priority.valueOf(resultSet.getString("priority")))
                    .profilePicture(resultSet.getString("profilePicture"))
                    .tags(
                        CSVToArrayConverter.convertCommaSeparated(
                            resultSet.getString("tags"), String::trim))
                    .build();

            tasks.add(task);
          }

          return tasks;
        });
  }

  @Override
  public String createTask(Task task) {
    try {
      String SQL =
          """
          INSERT INTO tasks
          (id, title, description, completed, priority, due_date, assigned_to, parent_id)
          VALUES (:id, :title, :description, :completed, :priority, :dueDate, :assignedTo,
          		  :parentId);
          """;

      SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(task);

      int rowsAffected = jdbcTemplate.update(SQL, parameterSource);

      return rowsAffected > 0 ? task.getId() : null;
    } catch (DataAccessException e) {
      log.error("Error creating task", e);
      throw e;
    } catch (Exception e) {
      log.error("Error creating task", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
