package com.event.tasker.DAO;

import com.event.tasker.model.Attachment;

public interface TaskAttachmentDao {
  Attachment getAttachment(String id);

  String createAttachment(Attachment attachment);

  String deleteAttachment(String id);

  String updateAttachment(Attachment attachment);
}
