package com.event.tasker.model;

import com.google.gson.annotations.Expose;

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
  @Expose private String url;
  @Expose private String fileName;
  @Expose private String fileType;
}
