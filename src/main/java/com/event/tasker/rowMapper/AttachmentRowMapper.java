package com.event.tasker.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.event.tasker.model.Attachment;

public class AttachmentRowMapper implements RowMapper<Attachment> {

  @Override
  public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Attachment.builder()
        .id(rs.getString("id"))
        .taskId(rs.getString("task_id"))
        .url(rs.getString("url"))
        .fileName(rs.getString("file_name"))
        .fileType(rs.getString("file_type"))
        .build();
  }
}
