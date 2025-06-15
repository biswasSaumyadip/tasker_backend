package com.event.tasker.DAO;

import java.util.Optional;

import com.event.tasker.model.Attachment;

public interface TaskAttachmentDao {
  Optional<Attachment> getAttachment(String id);

  String createAttachment(Attachment attachment);

  String deleteAttachment(String id, String taskId);

  String updateAttachment(Attachment attachment);
}
