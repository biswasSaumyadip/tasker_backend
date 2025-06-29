package com.event.tasker.DAO.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TaskDao;
import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.rowMapper.TaskDetailRowMapper;
import com.event.tasker.rowMapper.TaskRowMapper;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TaskDaoImpl implements TaskDao {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final Gson gson;

  private static final String TASK_BASE_SELECT =
      """
            SELECT t.id, t.title, t.description, t.completed, t.priority,
                   t.created_at AS createdAt, t.due_date AS dueDate,
                   t.parent_id AS parentId,
                   CONCAT(u.first_name, ' ', u.last_name) AS assignedTo,
                   u.profile_picture_url AS profilePicture,
                   GROUP_CONCAT(tt.tag) AS tags
            FROM tasks t
            LEFT JOIN task_tags tt ON t.id = tt.task_id
            LEFT JOIN users u ON u.user_id = t.assigned_to
            LEFT JOIN priority pr ON pr.id = t.priority
            """;

  private static final String TASK_GROUP_BY =
      "GROUP BY t.id, u.first_name, u.last_name, u.profile_picture_url";

  public ArrayList<Task> getTasks() {
    String sql = TASK_BASE_SELECT + " WHERE t.isDeleted = 0 " + TASK_GROUP_BY;
    return new ArrayList<>(jdbcTemplate.query(sql, new TaskRowMapper()));
  }

  @Override
  public String createTask(Task task) {
    try {
      String sql =
          """
                    INSERT INTO tasks
                    (id, title, description, completed, priority, due_date, assigned_to, parent_id)
                    VALUES (:id, :title, :description, :completed, :priority, :dueDate, :assignedTo,
                    		  :parentId);
                    """;

      MapSqlParameterSource parameterSource =
          new MapSqlParameterSource()
              .addValue("id", task.getId())
              .addValue("title", task.getTitle())
              .addValue("description", task.getDescription())
              .addValue("completed", task.isCompleted())
              .addValue("priority", task.getPriority().ordinal())
              .addValue("dueDate", task.getDueDate())
              .addValue("assignedTo", task.getAssignedTo())
              .addValue("parentId", task.getParentId());

      int rowsAffected = jdbcTemplate.update(sql, parameterSource);

      return rowsAffected > 0 ? task.getId() : null;
    } catch (DataAccessException e) {
      log.error("Error creating task", e);
      throw e;
    } catch (Exception e) {
      log.error("Error creating task", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public Optional<Task> getTask(String taskId) {
    final String sql =
        TASK_BASE_SELECT + " WHERE t.id = :taskId AND t.isDeleted = 0 " + TASK_GROUP_BY;

    SqlParameterSource parameters = new MapSqlParameterSource("taskId", taskId);

    try {
      Task task = jdbcTemplate.queryForObject(sql, parameters, new TaskRowMapper());
      return Optional.ofNullable(task);
    } catch (EmptyResultDataAccessException e) {
      log.warn("No task found with id: {}", taskId);
      return Optional.empty();
    } catch (DataAccessException e) {
      log.error("Error getting task", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Soft deletes a task by setting its 'deleted_at' timestamp to the current time. The record
   * remains in the database but is treated as inactive.
   *
   * @param taskId The ID of the task to delete.
   * @return true if the task was successfully deleted.
   */
  @Override
  public boolean softDeleteTaskById(String taskId) {
    final String sql =
        """
                 UPDATE tasks SET deletedAt = NOW(), isDeleted = 1
                 WHERE id = :taskId AND deletedAt IS NULL
                """;
    MapSqlParameterSource params = new MapSqlParameterSource("taskId", taskId);

    try {
      int rowsAffected = jdbcTemplate.update(sql, params);
      return rowsAffected > 0;
    } catch (DataAccessException e) {
      log.error("Error during soft delete for task id: {}", taskId, e);
      throw e;
    }
  }

  @Override
  public Optional<TaskDetail> getTaskDetail(String taskId) {
    String sql =
        """
                  SELECT t.id                                                     AS id,
                         t.title                                                  as title,
                         t.description                                            as description,
                         t.completed                                              as completed,
                         t.priority                                               as priority,
                         u.user_id                                                AS assignedTo,
                         t.created_at                                             AS createdAt,
                         t.due_date                                               AS dueDate,
                         u.profile_picture_url                                    AS profilePicture,
                         CONCAT(u.first_name, ' ', u.last_name)                   AS assignedToName,
                         parent_id                                                AS parentID,
                         GROUP_CONCAT(tt.tag)                                     AS tags,
                         MAX(COALESCE(attachments_agg.attachments, JSON_ARRAY())) AS attachments
                  FROM tasks t
                           LEFT JOIN task_tags tt ON t.id = tt.task_id
                           LEFT JOIN users u ON u.user_id = t.assigned_to
                           LEFT JOIN  priority pr ON pr.id = t.priority
                           LEFT JOIN
                       (SELECT ta.taskId,
                               JSON_ARRAYAGG(
                                       JSON_OBJECT(
                                               'url', ta.url,
                                               'fileName', ta.fileName,
                                               'fileType', ta.fileType
                                       )
                               ) AS attachments
                        FROM task_attachments ta
                        GROUP BY ta.taskId) AS attachments_agg ON t.id = attachments_agg.taskId
                  WHERE t.id = :taskId AND t.isDeleted = 0
                  GROUP BY t.id, u.first_name, u.last_name
                """;

    MapSqlParameterSource parameters = new MapSqlParameterSource("taskId", taskId);

    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(sql, parameters, new TaskDetailRowMapper(gson)));
    } catch (Exception e) {
      log.error("Error getting task detail", e);
      return Optional.empty();
    }
  }

  @Override
  public boolean updateTask(Task task) {
    final String sql =
        """
                UPDATE tasks
                SET priority    = :priority,
                completed   = :completed,
                description = :description,
                assigned_to = :assignedTo,
                title       = :title,
                due_date    = :due_date,
                parent_id   = :parent_id
            WHERE id = :id
            """;

    try {
      MapSqlParameterSource parameters = new MapSqlParameterSource();
      parameters.addValue("priority", task.getPriority().ordinal());
      parameters.addValue("completed", task.isCompleted());
      parameters.addValue("description", task.getDescription());
      parameters.addValue("title", task.getTitle());
      parameters.addValue("due_date", task.getDueDate());
      parameters.addValue("assignedTo", task.getAssignedTo());
      parameters.addValue("parent_id", task.getParentId());
      parameters.addValue("id", task.getId());

      return jdbcTemplate.update(sql, parameters) >= 1;
    } catch (DataAccessException e) {
      log.error("Error creating task", e);
      throw e;
    }
  }
}
