package com.event.tasker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskerResponse<T> {
  private T data;
  private String message;
  private String error;
  private String status;
  private String errorCode;

  public static <T> TaskerResponse<T> success(T data, String message) {
    return TaskerResponse.<T>builder().data(data).message(message).status("SUCCESS").build();
  }

  public static <T> TaskerResponse<T> failure(String error, String errorCode) {
    return TaskerResponse.<T>builder().error(error).errorCode(errorCode).status("ERROR").build();
  }
}
