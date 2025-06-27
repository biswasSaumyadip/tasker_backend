package com.event.tasker.DAO.impl;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TaskTagDao;
import com.event.tasker.model.TaskTag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TaskTagDaoImpl implements TaskTagDao {

  private final NamedParameterJdbcTemplate jdbcTemplate;

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
}
