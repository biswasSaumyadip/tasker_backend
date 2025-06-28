package com.event.tasker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.event.tasker.DAO.TaskAttachmentDao;
import com.event.tasker.DAO.TaskTagDao;
import com.event.tasker.DAO.impl.TaskDaoImpl;
import com.event.tasker.model.Attachment;
import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.model.TaskerResponse;
import com.event.tasker.service.FileStorageService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskServiceImpl")
class TaskServiceImplTest {

  @Mock private TaskDaoImpl taskDao;
  @Mock private TaskAttachmentDao taskAttachmentDao;
  @Mock private TaskTagDao taskTagDao;
  @Mock private FileStorageService fileStorageService;
  @Mock private TransactionTemplate transactionTemplate;
  @Mock private MultipartFile mockFile;

  @Mock private Logger logger;

  @Mock MultipartFile file;

  @InjectMocks private TaskServiceImpl taskService;

  TaskDetail taskDetail;
  Task task;

  @BeforeEach
  void setUp() {
    taskDetail =
        TaskDetail.builder()
            .id("task-123")
            .title("Test Task")
            .description("Test Description")
            .priority(Task.Priority.HIGH)
            .assignedTo("user-1")
            .tags(new ArrayList<>(List.of("tag1", "tag2")))
            .attachments(new ArrayList<>())
            .build();
  }

  @Test
  @DisplayName("Get tasks returns list of tasks successfully")
  void testGetTasksSuccess() {
    // Given
    ArrayList<Task> expectedTasks = new ArrayList<>();
    Task task1 =
        Task.builder()
            .id("1")
            .title("Task 1")
            .description("Description 1")
            .completed(false)
            .priority(Task.Priority.LOW)
            .dueDate(Instant.now())
            .createdAt(Instant.now())
            .assignedTo("user1")
            .parentId(null)
            .tags(Arrays.asList("tag1", "tag2"))
            .build();
    expectedTasks.add(task1);

    when(taskDao.getTasks()).thenReturn(expectedTasks);

    // When
    ArrayList<Task> actualTasks = taskService.getTasks();

    // Then
    assertNotNull(actualTasks);
    assertEquals(expectedTasks, actualTasks);
    verify(taskDao).getTasks();
    verifyNoInteractions(logger);
  }

  @Test
  @DisplayName("Get tasks returns empty list when no tasks exist")
  void testGetTasksWithEmptyList() {
    // Given
    ArrayList<Task> expectedTasks = new ArrayList<>();
    when(taskDao.getTasks()).thenReturn(expectedTasks);

    // When
    ArrayList<Task> actualTasks = taskService.getTasks();

    // Then
    assertNotNull(actualTasks);
    assertTrue(actualTasks.isEmpty());
    verify(taskDao).getTasks();
    verifyNoInteractions(logger);
  }

  @Test
  @DisplayName("Get tasks returns null when database error occurs")
  void testGetTasksWithException() {
    // Given
    RuntimeException expectedException = new RuntimeException("Database error");
    when(taskDao.getTasks()).thenThrow(expectedException);

    // When
    ArrayList<Task> actualTasks = taskService.getTasks();

    // Then
    assertNull(actualTasks);
    verify(taskDao).getTasks();
    // Note: We can't easily verify the logging with Lombok's @Slf4j
    // as it creates a private static final logger
  }

  @Test
  @DisplayName("addAttachments: should upload files successfully")
  void testAddAttachmentsSuccess() throws IOException {
    // Given
    String taskId = "task-123";
    List<MultipartFile> files = Arrays.asList(mockFile, mockFile);
    String uniqueFileName = "unique-file-name";
    Attachment mockAttachment = Attachment.builder().id("att-1").taskId(taskId).build();

    when(mockFile.isEmpty()).thenReturn(false);
    when(fileStorageService.uploadFile(mockFile)).thenReturn(uniqueFileName);
    when(fileStorageService.getFileMetadata(uniqueFileName)).thenReturn(mockAttachment);

    // When
    taskService.addAttachments(taskId, files);

    // Then
    verify(fileStorageService, times(2)).uploadFile(mockFile);
    verify(fileStorageService, times(2)).getFileMetadata(uniqueFileName);
    verify(taskAttachmentDao, times(2)).createAttachment(mockAttachment);
  }

  @Test
  @DisplayName("addAttachments: should do nothing when files list is empty")
  void testAddAttachmentsWithEmptyFilesList() {
    // Given
    String taskId = "task-123";
    List<MultipartFile> emptyFiles = Collections.emptyList();

    // When
    taskService.addAttachments(taskId, emptyFiles);

    // Then
    verifyNoInteractions(fileStorageService);
    verifyNoInteractions(taskAttachmentDao);
  }

  @Test
  @DisplayName("addAttachments: should do nothing when files list is null")
  void testAddAttachmentsWithNullFilesList() {
    // Given
    String taskId = "task-123";

    // When
    taskService.addAttachments(taskId, null);

    // Then
    verifyNoInteractions(fileStorageService);
    verifyNoInteractions(taskAttachmentDao);
  }

  @Test
  @DisplayName("addAttachments: should skip empty files")
  void testAddAttachmentsSkipsEmptyFiles() throws IOException {
    // Given
    String taskId = "task-123";
    List<MultipartFile> files = List.of(mockFile);
    when(mockFile.isEmpty()).thenReturn(true);

    // When
    taskService.addAttachments(taskId, files);

    // Then
    verify(fileStorageService, never()).uploadFile(any());
    verify(taskAttachmentDao, never()).createAttachment(any());
  }

  @Test
  @DisplayName("addAttachments: should throw RuntimeException when file upload fails")
  void testAddAttachmentsThrowsExceptionOnUploadFailure() throws IOException {
    // Given
    String taskId = "task-123";
    List<MultipartFile> files = List.of(mockFile);
    IOException uploadException = new IOException("Upload failed");

    when(mockFile.isEmpty()).thenReturn(false);
    when(fileStorageService.uploadFile(mockFile)).thenThrow(uploadException);
    when(mockFile.getOriginalFilename()).thenReturn("test-file.txt");

    // When & Then
    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> taskService.addAttachments(taskId, files));

    assertTrue(thrown.getMessage().contains("Failed to store file"));
    assertEquals(uploadException, thrown.getCause());
  }

  @Test
  @DisplayName("addTask: should create task successfully")
  void testAddTaskSuccess() {
    // Given
    String taskId = "task-123";
    List<String> tags = Arrays.asList("tag1", "tag2");
    TaskDetail taskDetail =
        TaskDetail.builder()
            .id(taskId)
            .title("Test Task")
            .description("Test Description")
            .tags(tags)
            .build();

    String insertStatus = "SUCCESS";

    // Mock transaction execution to actually execute the lambda
    when(transactionTemplate.execute(any()))
        .thenAnswer(
            invocation -> {
              // Get the lambda function passed to execute()
              org.springframework.transaction.support.TransactionCallback<String> callback =
                  invocation.getArgument(0);
              // Execute the lambda and return the result
              when(taskDao.createTask(any(Task.class))).thenReturn(insertStatus);
              return callback.doInTransaction(null);
            });

    // When
    taskService.addTask(taskDetail, null);

    // Then
    verify(taskDao).createTask(any(Task.class));
    verify(taskTagDao).createTaskTags(any());
    // Since files is null, addAttachments should not interact with fileStorageService
    verifyNoInteractions(fileStorageService);
  }

  @Test
  @DisplayName("addTask: should create task with attachments successfully")
  void testAddTaskWithAttachmentsSuccess() throws IOException {
    // Given
    String taskId = "task-123";
    List<String> tags = Arrays.asList("tag1", "tag2");
    List<Attachment> attachments =
        Collections.singletonList(Attachment.builder().id("att-1").taskId(taskId).build());

    TaskDetail taskDetail =
        TaskDetail.builder()
            .id(taskId)
            .title("Test Task")
            .description("Test Description")
            .tags(tags)
            .attachments(attachments)
            .build();

    List<MultipartFile> files = List.of(mockFile);
    String insertStatus = "SUCCESS";
    String uniqueFileName = "unique-file-name";

    // Mock transaction execution to actually execute the lambda
    when(transactionTemplate.execute(any()))
        .thenAnswer(
            invocation -> {
              // Get the lambda function passed to execute()
              org.springframework.transaction.support.TransactionCallback<String> callback =
                  invocation.getArgument(0);
              // Execute the lambda and return the result
              when(taskDao.createTask(any(Task.class))).thenReturn(insertStatus);
              return callback.doInTransaction(null);
            });

    // Mock file upload
    when(mockFile.isEmpty()).thenReturn(false);
    when(fileStorageService.uploadFile(mockFile)).thenReturn(uniqueFileName);
    when(fileStorageService.getFileMetadata(uniqueFileName))
        .thenReturn(Attachment.builder().id("att-1").taskId(taskId).build());

    // When
    taskService.addTask(taskDetail, files);

    // Then
    verify(taskDao).createTask(any(Task.class));
    verify(taskTagDao).createTaskTags(any());
    verify(fileStorageService).uploadFile(mockFile);
    verify(taskAttachmentDao).createAttachment(any(Attachment.class));
  }

  @Test
  @DisplayName("addTask: should return partial success when task is created but file upload fails")
  void testAddTaskPartialSuccess() throws IOException {
    // Given
    String taskId = "task-123";
    List<String> tags = Arrays.asList("tag1", "tag2");
    List<Attachment> attachments =
        Collections.singletonList(Attachment.builder().id("att-1").taskId(taskId).build());

    TaskDetail taskDetail =
        TaskDetail.builder()
            .id(taskId)
            .title("Test Task")
            .description("Test Description")
            .tags(tags)
            .attachments(attachments)
            .build();

    List<MultipartFile> files = List.of(mockFile);
    String insertStatus = "SUCCESS";
    IOException uploadException = new IOException("Upload failed");

    // Mock transaction execution to actually execute the lambda
    when(transactionTemplate.execute(any()))
        .thenAnswer(
            invocation -> {
              // Get the lambda function passed to execute()
              org.springframework.transaction.support.TransactionCallback<String> callback =
                  invocation.getArgument(0);
              // Execute the lambda and return the result
              when(taskDao.createTask(any(Task.class))).thenReturn(insertStatus);
              return callback.doInTransaction(null);
            });

    // Mock file upload failure
    when(mockFile.isEmpty()).thenReturn(false);
    when(fileStorageService.uploadFile(mockFile)).thenThrow(uploadException);
    when(mockFile.getOriginalFilename()).thenReturn("test-file.txt");

    // When
    TaskerResponse<String> response = taskService.addTask(taskDetail, files);

    // Then
    verify(taskDao).createTask(any(Task.class));
    verify(taskTagDao).createTaskTags(any());

    // Verify response indicates partial success
    assertNotNull(response);
    assertEquals("PARTIAL_SUCCESS", response.getStatus());
    assertEquals("Task created, but file attachment failed.", response.getMessage());
    assertEquals(insertStatus, response.getData());
  }

  @Test
  @DisplayName("deleteTask: should return DELETED status when task is deleted successfully")
  void testDeleteTaskSuccess() {
    // Given
    String taskId = "task-123";
    when(taskDao.softDeleteTaskById(taskId)).thenReturn(true);

    // When
    TaskerResponse<String> response = taskService.deleteTask(taskId);

    // Then
    assertNotNull(response);
    assertEquals("DELETED", response.getStatus());
    assertEquals("Task deleted", response.getMessage());
    assertNull(response.getData()); // Since delete does not return data
    verify(taskDao).softDeleteTaskById(taskId);
  }

  @Test
  @DisplayName("deleteTask: should return 'Task not found' when deletion returns false")
  void testDeleteTaskNotFound() {
    // Given
    String taskId = "task-456";
    when(taskDao.softDeleteTaskById(taskId)).thenReturn(false);

    // When
    TaskerResponse<String> response = taskService.deleteTask(taskId);

    // Then
    assertNotNull(response);
    assertNull(response.getStatus());
    assertEquals("Task not found", response.getMessage());
    assertNull(response.getData());
    verify(taskDao).softDeleteTaskById(taskId);
  }

  @Test
  @DisplayName("deleteTask: should throw exception when soft delete fails with exception")
  void testDeleteTaskThrowsException() {
    // Given
    String taskId = "task-error";
    RuntimeException ex = new RuntimeException("Database failure");
    when(taskDao.softDeleteTaskById(taskId)).thenThrow(ex);

    // When & Then
    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> taskService.deleteTask(taskId));

    assertEquals(ex, thrown);
    verify(taskDao).softDeleteTaskById(taskId);
  }

  @Test
  @DisplayName("updateTask: All components update successfully")
  void updateTask_ShouldUpdateAllAndReturnSuccess() throws IOException {
    // given
    ArrayList<Attachment> existingAttachments =
        new ArrayList<>(
            List.of(
                Attachment.builder().id("old-1").build(),
                Attachment.builder().id("old-2").build()));
    ArrayList<Attachment> newAttachments =
        new ArrayList<>(List.of(Attachment.builder().id("old-1").build()));
    taskDetail.setAttachments(newAttachments);

    when(taskDao.updateTask(any(Task.class))).thenReturn(true);
    when(fileStorageService.uploadFile(file)).thenReturn("uniqueFileName.txt");
    when(fileStorageService.getFileMetadata(anyString()))
        .thenReturn(Attachment.builder().id("old-1").build());
    when(taskTagDao.getTaskTagsBy("task-123"))
        .thenReturn(new ArrayList<>(List.of("tag1", "tagOld")));
    when(taskAttachmentDao.getAttachmentsBy("task-123")).thenReturn(existingAttachments);
    when(taskAttachmentDao.softDeleteAttachmentsBy(new ArrayList<>(List.of("old-2"))))
        .thenReturn("Soft-deleted 1 attachments");

    // when
    TaskerResponse<String> response = taskService.updateTask(taskDetail, List.of(file));

    // then
    assertEquals("Task task-123 updated", response.getMessage());
    verify(taskDao).updateTask(any(Task.class));
    verify(taskAttachmentDao).softDeleteAttachmentsBy(new ArrayList<>(List.of("old-2")));
    verify(taskTagDao).deleteTaskTags(any(), eq("task-123"));
    verify(taskTagDao).createTaskTags(any());
  }

  @Test
  @DisplayName("updateTask: should return failure message if task update fails")
  void testUpdateTask_TaskUpdateFails() {
    // Arrange
    when(taskDao.updateTask(any(Task.class))).thenReturn(false);
    when(taskTagDao.getTaskTagsBy("task-123"))
        .thenReturn(new ArrayList<>(List.of("tag1", "tagOld")));

    // Act
    TaskerResponse<String> response = taskService.updateTask(taskDetail, List.of());

    // Assert
    assertEquals("Task update failed.", response.getMessage());
    verify(taskDao).updateTask(any(Task.class));
  }
}
