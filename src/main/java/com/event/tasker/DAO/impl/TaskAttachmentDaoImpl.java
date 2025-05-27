package com.event.tasker.DAO.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TaskAttachmentDao;
import com.event.tasker.model.Attachment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TaskAttachmentDaoImpl implements TaskAttachmentDao {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public Attachment getAttachment(String id) {
    return null;
  }

  @Override
  public String createAttachment(Attachment attachment) {
    try {
      String SQL =
          """
				INSERT INTO TASK_ATTACHMENTS (id, taskId, url, fileName, fileType)
				VALUES (:id, :taskId, :url,
								:fileName, :fileType)
				""";
      SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(attachment);

      int rowsAffected = jdbcTemplate.update(SQL, parameterSource);
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
  public String deleteAttachment(String id) {
    return "";
  }

  @Override
  public String updateAttachment(Attachment attachment) {
    return "";
  }
}
