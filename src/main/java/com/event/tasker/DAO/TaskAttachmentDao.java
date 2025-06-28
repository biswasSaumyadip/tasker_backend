package com.event.tasker.DAO;

import java.util.ArrayList;
import java.util.Optional;

import com.event.tasker.model.Attachment;

public interface TaskAttachmentDao {
  Optional<Attachment> getAttachment(String id);

  String createAttachment(Attachment attachment);

  String deleteAttachment(String id, String taskId);

  String updateAttachment(Attachment attachment);

  String softDeleteAttachment(String id);

  ArrayList<Attachment> getAttachmentsBy(String taskId);

  String softDeleteAttachmentsBy(ArrayList<String> ids);
}
