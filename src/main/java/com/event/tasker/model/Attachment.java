package com.event.tasker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
  private String id;
  private String taskId;
  private String url;
  private String fileName;
  private String fileType;
}
