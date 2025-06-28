package com.event.tasker.DAO.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TaskTagDao;
import com.event.tasker.model.TaskTag;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TaskTagDaoImpl implements TaskTagDao {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final Gson gson;

  @Override
  public int createTaskTags(ArrayList<TaskTag> tags) {
    if (tags == null || tags.isEmpty()) {
      assert tags != null;
      return 0;
    }

    String sql = """
				INSERT INTO task_tags (task_id, tag)
				VALUES (:taskId, :name);
				""";

    SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(tags.toArray());

    try {
      int[] result = jdbcTemplate.batchUpdate(sql, batchParams);

      return Arrays.stream(result).sum();
    } catch (DataAccessException e) {
      log.error("Error creating task tags", e);
      throw e;
    }
  }

  @Override
  public int deleteTaskTags(ArrayList<TaskTag> tags, String taskId) {

    if (tags == null || tags.isEmpty()) {
      log.warn("No tags provided to delete for taskId {}", taskId);
      return 0;
    }

    String sql =
        """
            UPDATE task_tags
            SET isDeleted = 1
            WHERE task_id = :taskId
              AND tag in (:tags)
          """;

    try {

      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("taskId", taskId);
      params.addValue("tags", tags.stream().map(TaskTag::getName).collect(Collectors.toList()));

      return jdbcTemplate.update(sql, params);
    } catch (DataAccessException e) {
      log.error("Error deleting task tags", e);
      throw e;
    }
  }

  @Override
  public ArrayList<String> getTaskTagsBy(String uuid) {
    String sql =
        """
            SELECT JSON_ARRAYAGG(tag) as tags
            FROM task_tags
            WHERE task_id = :taskId
              AND isDeleted = 0;
          """;

    try {
      return jdbcTemplate.query(
          sql,
          new MapSqlParameterSource().addValue("taskId", uuid),
          (rs -> {
            if (!rs.next()) return new ArrayList<>();

            String json = rs.getString("tags");
            if (json == null || json.isBlank()) return new ArrayList<>();

            try {
              return gson.fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());
            } catch (JsonSyntaxException ex) {
              log.error("Malformed JSON in tag list: {}", json, ex);
              return new ArrayList<>();
            }
          }));
    } catch (DataAccessException e) {
      log.error("Error getting task tags", e);
      throw e;
    }
  }
}
