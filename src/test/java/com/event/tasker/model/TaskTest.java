package com.event.tasker.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskTest {

  @Test
  @DisplayName("Test testTaskBuilder: Object should create successfully")
  void testTaskBuilder() {
    // Given
    String id = "123";
    String title = "Test Task";
    String description = "Test Description";
    boolean completed = false;
    Task.Priority priority = Task.Priority.MEDIUM;
    Instant dueDate = Instant.now().plusSeconds(86400); // tomorrow
    Instant createdAt = Instant.now();
    String assignedTo = "user1";
    String parentId = "parent123";
    List<String> tags = Arrays.asList("important", "work");

    // When
    Task task =
        Task.builder()
            .id(id)
            .title(title)
            .description(description)
            .completed(completed)
            .priority(priority)
            .dueDate(dueDate)
            .createdAt(createdAt)
            .assignedTo(assignedTo)
            .parentId(parentId)
            .tags(tags)
            .build();

    // Then
    assertEquals(id, task.getId());
    assertEquals(title, task.getTitle());
    assertEquals(description, task.getDescription());
    assertEquals(completed, task.isCompleted());
    assertEquals(priority, task.getPriority());
    assertEquals(dueDate, task.getDueDate());
    assertEquals(createdAt, task.getCreatedAt());
    assertEquals(assignedTo, task.getAssignedTo());
    assertEquals(parentId, task.getParentId());
    assertEquals(tags, task.getTags());
  }

  @Test
  void testGettersAndSetters() {
    // Given
    Task task = new Task();
    String id = "456";
    String title = "Another Task";
    String description = "Another Description";
    boolean completed = true;
    Task.Priority priority = Task.Priority.HIGH;
    Instant dueDate = Instant.now().plusSeconds(172800); // 2 days later
    Instant createdAt = Instant.now();
    String assignedTo = "user2";
    String parentId = "parent456";
    List<String> tags = Arrays.asList("urgent", "meeting");

    // When
    task.setId(id);
    task.setTitle(title);
    task.setDescription(description);
    task.setCompleted(completed);
    task.setPriority(priority);
    task.setDueDate(dueDate);
    task.setCreatedAt(createdAt);
    task.setAssignedTo(assignedTo);
    task.setParentId(parentId);
    task.setTags(tags);

    // Then
    assertEquals(id, task.getId());
    assertEquals(title, task.getTitle());
    assertEquals(description, task.getDescription());
    assertEquals(completed, task.isCompleted());
    assertEquals(priority, task.getPriority());
    assertEquals(dueDate, task.getDueDate());
    assertEquals(createdAt, task.getCreatedAt());
    assertEquals(assignedTo, task.getAssignedTo());
    assertEquals(parentId, task.getParentId());
    assertEquals(tags, task.getTags());
  }

  @Test
  void testEqualsAndHashCode() {
    // Given
    Instant now = Instant.now();
    Task task1 =
        Task.builder()
            .id("123")
            .title("Test Task")
            .description("Description")
            .completed(false)
            .priority(Task.Priority.LOW)
            .dueDate(now)
            .createdAt(now)
            .assignedTo("user1")
            .parentId("parent123")
            .tags(Arrays.asList("tag1", "tag2"))
            .build();

    Task task2 =
        Task.builder()
            .id("123")
            .title("Test Task")
            .description("Description")
            .completed(false)
            .priority(Task.Priority.LOW)
            .dueDate(now)
            .createdAt(now)
            .assignedTo("user1")
            .parentId("parent123")
            .tags(Arrays.asList("tag1", "tag2"))
            .build();

    Task task3 =
        Task.builder()
            .id("456")
            .title("Different Task")
            .description("Different Description")
            .completed(true)
            .priority(Task.Priority.HIGH)
            .dueDate(now.plusSeconds(86400))
            .createdAt(now)
            .assignedTo("user2")
            .parentId("parent456")
            .tags(Arrays.asList("tag3", "tag4"))
            .build();

    // Then
    assertEquals(task1, task2);
    assertEquals(task1.hashCode(), task2.hashCode());
    assertNotEquals(task1, task3);
    assertNotEquals(task1.hashCode(), task3.hashCode());
  }

  @Test
  void testToString() {
    // Given
    Instant now = Instant.now();
    Task task =
        Task.builder()
            .id("123")
            .title("Test Task")
            .description("Description")
            .completed(false)
            .priority(Task.Priority.LOW)
            .dueDate(now)
            .createdAt(now)
            .assignedTo("user1")
            .parentId("parent123")
            .tags(Arrays.asList("tag1", "tag2"))
            .build();

    // When
    String taskString = task.toString();

    // Then
    assertTrue(taskString.contains("id=123"));
    assertTrue(taskString.contains("title=Test Task"));
    assertTrue(taskString.contains("description=Description"));
    assertTrue(taskString.contains("completed=false"));
    assertTrue(taskString.contains("priority=LOW"));
    assertTrue(taskString.contains("assignedTo=user1"));
    assertTrue(taskString.contains("parentId=parent123"));
    assertTrue(taskString.contains("tags=[tag1, tag2]"));
  }

  @Test
  void testAllArgsConstructor() {
    // Given
    String id = "789";
    String title = "Constructor Task";
    String description = "Constructor Description";
    boolean completed = true;
    Task.Priority priority = Task.Priority.HIGH;
    Instant dueDate = Instant.now().plusSeconds(259200); // 3 days later
    Instant createdAt = Instant.now();
    String assignedTo = "user3";
    String parentId = "parent789";
    List<String> tags = Arrays.asList("constructor", "test");

    // When
    Task task =
        new Task(
            id,
            title,
            description,
            completed,
            priority,
            dueDate,
            createdAt,
            assignedTo,
            parentId,
            tags);

    // Then
    assertEquals(id, task.getId());
    assertEquals(title, task.getTitle());
    assertEquals(description, task.getDescription());
    assertEquals(completed, task.isCompleted());
    assertEquals(priority, task.getPriority());
    assertEquals(dueDate, task.getDueDate());
    assertEquals(createdAt, task.getCreatedAt());
    assertEquals(assignedTo, task.getAssignedTo());
    assertEquals(parentId, task.getParentId());
    assertEquals(tags, task.getTags());
  }

  @Test
  void testNoArgsConstructor() {
    // When
    Task task = new Task();

    // Then
    assertNull(task.getId());
    assertNull(task.getTitle());
    assertNull(task.getDescription());
    assertFalse(task.isCompleted());
    assertNull(task.getPriority());
    assertNull(task.getDueDate());
    assertNull(task.getCreatedAt());
    assertNull(task.getAssignedTo());
    assertNull(task.getParentId());
    assertNull(task.getTags());
  }
}
