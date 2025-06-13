package com.event.tasker.DAO.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TaskDao;
import com.event.tasker.model.Attachment;
import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.rowMapper.TaskRowMapper;
import com.event.tasker.util.CSVToArrayConverter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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
        """;

  private static final String TASK_DETAIL_ATTACHMENT_JOIN =
      """
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
        """;

  private static final String TASK_GROUP_BY =
      "GROUP BY t.id, u.first_name, u.last_name, u.profile_picture_url";

  public ArrayList<Task> getTasks() {
    String sql = TASK_BASE_SELECT + " " + TASK_GROUP_BY;
    return new ArrayList<>(jdbcTemplate.query(sql, new TaskRowMapper()));
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

      MapSqlParameterSource parameterSource =
          new MapSqlParameterSource()
              .addValue("id", task.getId())
              .addValue("title", task.getTitle())
              .addValue("description", task.getDescription())
              .addValue("completed", task.isCompleted())
              .addValue("priority", task.getPriority().name())
              .addValue("dueDate", task.getDueDate())
              .addValue("assignedTo", task.getAssignedTo())
              .addValue("parentId", task.getParentId());

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

  @Override
  public Task getTask(String taskId) {
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
            + "WHERE t.id = :taskId\n "
            + "GROUP BY t.id, u.first_name, u.last_name";

    SqlParameterSource parameters = new MapSqlParameterSource("taskId", taskId);

    return jdbcTemplate.query(
        sql,
        parameters,
        resultset -> {
          Task task = new Task();
          while (resultset.next()) {
            task =
                Task.builder()
                    .id(resultset.getString("id"))
                    .assignedTo(resultset.getString("assignedTo"))
                    .title(resultset.getString("title"))
                    .createdAt(resultset.getTimestamp("createdAt").toInstant())
                    .dueDate(resultset.getTimestamp("dueDate").toInstant())
                    .profilePicture(resultset.getString("profilePicture"))
                    .parentId(resultset.getString("parentId"))
                    .tags(
                        CSVToArrayConverter.convertCommaSeparated(
                            resultset.getString("tags"), String::trim))
                    .build();
          }
          return task;
        });
  }

  @Override
  public boolean deleteTaskById(String taskId) {
    String sql = "DELETE FROM tasks WHERE id = :taskId";
    return false;
  }

  @Override
  public TaskDetail getTaskDetail(String taskId) {
    String sql =
        """
            SELECT t.id                                                     AS id,
                   t.title                                                  as title,
                   t.description                                            as description,
                   t.completed                                              as completed,
                   t.priority                                               as priority,
                   CONCAT(u.first_name, ' ', u.last_name)                   AS assignedTo,
                   t.created_at                                             AS createdAt,
                   t.due_date                                               AS dueDate,
                   u.profile_picture_url                                    AS profilePicture,
                   parent_id                                                AS parentID,
                   GROUP_CONCAT(tt.tag)                                     AS tags,
                   MAX(COALESCE(attachments_agg.attachments, JSON_ARRAY())) AS attachments
            FROM tasks t
                     LEFT JOIN task_tags tt ON t.id = tt.task_id
                     LEFT JOIN users u ON u.user_id = t.assigned_to
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
            WHERE t.id = :taskId
            GROUP BY t.id, u.first_name, u.last_name
          """;

    MapSqlParameterSource parameters = new MapSqlParameterSource("taskId", taskId);

    return jdbcTemplate.query(
        sql,
        parameters,
        resultSet -> {
          TaskDetail taskDetail = new TaskDetail();
          while (resultSet.next()) {
            // TODO: handle json exception here
            String attachmentString = resultSet.getString("attachments");
            JsonArray attachmentJson = gson.fromJson(attachmentString, JsonArray.class);
            List<Attachment> attachments = new ArrayList<>();

            for (JsonElement jsonElement : attachmentJson) {
              attachments.add(gson.fromJson(jsonElement, Attachment.class));
            }

            taskDetail =
                TaskDetail.builder()
                    .id(resultSet.getString("id"))
                    .assignedTo(resultSet.getString("assignedTo"))
                    .title(resultSet.getString("title"))
                    .description(resultSet.getString("description"))
                    .completed(resultSet.getBoolean("completed"))
                    .priority(Task.Priority.valueOf(resultSet.getString("priority")))
                    .dueDate(resultSet.getTimestamp("dueDate").toInstant())
                    .parentId(resultSet.getString("parentId"))
                    .tags(
                        CSVToArrayConverter.convertCommaSeparated(
                            resultSet.getString("tags"), String::trim))
                    .attachments(attachments)
                    .build();
          }

          return taskDetail;
        });
  }
}
