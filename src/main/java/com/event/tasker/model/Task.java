package com.event.tasker.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
  private String id; // UUID or string
  private String title;
  private String description;
  private boolean completed;

  @Getter
  public enum Priority {
    ALL(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    private final int code;

    Priority(int code) {
      this.code = code;
    }

    public static Priority fromCode(int code) {
      for (Priority p : values()) {
        if (p.code == code) return p;
      }
      throw new IllegalArgumentException("Unknown priority code: " + code);
    }
  }

  private Priority priority;

  private Instant dueDate; // ISO 8601 format
  private Instant createdAt; // ISO 8601
  private String assignedTo; // user ID or reference
  private String parentId; // null if top-level
  private List<String> tags; // optional categorization
  private String profilePicture;
}
