package com.event.tasker.DAO.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TaskAttachmentDao;
import com.event.tasker.model.Attachment;
import com.event.tasker.rowMapper.AttachmentRowMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TaskAttachmentDaoImpl implements TaskAttachmentDao {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public Optional<Attachment> getAttachment(String id) {
    String sql =
        """
          SELECT url, fileName, fileType, uploadedAt
          FROM task_attachments
          WHERE taskId = :taskId AND isDeleted = 0
          """;

    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              sql, new MapSqlParameterSource("taskId", id), new AttachmentRowMapper()));
    } catch (DataAccessException e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  @Override
  public String createAttachment(Attachment attachment) {
    try {
      String sql =
          """
				INSERT INTO task_attachments (id, taskId, url, fileName, fileType)
				VALUES (:id, :taskId, :url,
								:fileName, :fileType)
				""";
      SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(attachment);

      int rowsAffected = jdbcTemplate.update(sql, parameterSource);
      if (rowsAffected > 0) {
        return attachment.getId();
      } else {
        log.error("Error creating attachment");
        throw new RuntimeException("Error creating attachment");
      }
    } catch (DataAccessException e) {
      log.error("Error creating attachment", e);
      throw e;
    } catch (Exception e) {
      log.error("Error creating attachment", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public String deleteAttachment(String id, String taskId) {
    String sqlDeleteByTaskId =
        """
        DELETE
        FROM task_attachments
        WHERE taskId = :taskId
        """;

    String sqlDeleteById =
        """
        DELETE
        FROM task_attachments
        WHERE id = :id
        """;

    try {
      if (taskId != null) {
        return jdbcTemplate.update(sqlDeleteByTaskId, new MapSqlParameterSource("taskId", taskId))
                >= 1
            ? taskId
            : null;
      }

      if (id != null) {
        return jdbcTemplate.update(sqlDeleteById, new MapSqlParameterSource("id", id)) >= 1
            ? id
            : null;
      }

      return "";
    } catch (DataAccessException e) {
      log.error("Error deleting attachment", e);
      throw e;
    }
  }

  @Override
  public String updateAttachment(Attachment attachment) {
    try {
      String sql =
          """
          UPDATE task_attachments
          SET url      = :url,
              fileName = :fileName,
              fileType = :fileType
          WHERE taskId = :taskId
          """;

      MapSqlParameterSource parameterSource = new MapSqlParameterSource();
      parameterSource.addValue("url", attachment.getUrl());
      parameterSource.addValue("fileName", attachment.getFileName());
      parameterSource.addValue("fileType", attachment.getFileType());
      parameterSource.addValue("taskId", attachment.getTaskId());

      return jdbcTemplate.update(sql, parameterSource) >= 1 ? attachment.getId() : null;
    } catch (DataAccessException e) {
      log.error("Error updating attachment", e);
      throw e;
    }
  }

  @Override
  public String softDeleteAttachment(String id) {
    String sql =
        """
          UPDATE task_attachments
          SET isDeleted = 1
          WHERE id = :id
          """;

    try {
      return jdbcTemplate.update(sql, new MapSqlParameterSource("id", id)) >= 1 ? id : null;
    } catch (DataAccessException e) {
      log.error("Error softDeleting attachment", e);
      throw e;
    }
  }

  @Override
  public ArrayList<Attachment> getAttachmentsBy(String taskId) {
    String sql =
        """
              SELECT url, fileName, fileType, uploadedAt
              FROM task_attachments
              WHERE taskId = :taskId AND isDeleted = 0
              """;

    try {
      return jdbcTemplate.query(
          sql,
          new MapSqlParameterSource("taskId", taskId),
          rs -> {
            ArrayList<Attachment> attachments = new ArrayList<>();
            if (rs.next()) {
              attachments.add(
                  Attachment.builder()
                      .url(rs.getString("url"))
                      .fileName(rs.getString("fileName"))
                      .fileType(rs.getString("fileType"))
                      .id(rs.getString("id"))
                      .taskId(rs.getString("taskId"))
                      .build());
            }

            return attachments;
          });
    } catch (DataAccessException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public String softDeleteAttachmentsBy(ArrayList<String> ids) {
    if (ids == null || ids.isEmpty()) {
      log.warn("No attachment IDs provided for soft delete.");
      return "No IDs provided";
    }

    String sql =
        """
        UPDATE task_attachments
        SET isDeleted = 1
        WHERE id = :id
        """;

    List<MapSqlParameterSource> params =
        ids.stream().map(id -> new MapSqlParameterSource("id", id)).toList();

    try {
      int[] result = jdbcTemplate.batchUpdate(sql, params.toArray(new SqlParameterSource[0]));
      int updatedCount = Arrays.stream(result).sum();

      log.info("Soft-deleted {} attachments", updatedCount);
      return "Soft-deleted " + updatedCount + " attachments";
    } catch (DataAccessException ex) {
      log.error("Error performing batch soft delete of attachments", ex);
      throw ex; // or return a failure message if preferred
    }
  }
}
