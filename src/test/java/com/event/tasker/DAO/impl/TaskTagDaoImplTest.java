package com.event.tasker.DAO.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.event.tasker.model.TaskTag;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskTagDaoImpl")
public class TaskTagDaoImplTest {

  @InjectMocks TaskTagDaoImpl taskTagDao; // your DAO class

  @Mock NamedParameterJdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("createTaskTags: should return total rows inserted on successful batch insert")
  void testCreateTaskTagsSuccess() {
    // Arrange
    ArrayList<TaskTag> tags = new ArrayList<>();
    tags.add(new TaskTag("Random1L", "tag1"));
    tags.add(new TaskTag("Random2L", "tag2"));

    int[] batchResult = new int[] {1, 1};
    when(jdbcTemplate.batchUpdate(anyString(), any(SqlParameterSource[].class)))
        .thenReturn(batchResult);

    // Act
    int insertedCount = taskTagDao.createTaskTags(tags);

    // Assert
    assertEquals(2, insertedCount, "Total inserted rows should be sum of batch results");
    verify(jdbcTemplate).batchUpdate(anyString(), any(SqlParameterSource[].class));
  }

  @Test
  @DisplayName("createTaskTags: should propagate DataAccessException on DB errors")
  void testCreateTaskTagsThrowsDataAccessException() {
    // Arrange
    ArrayList<TaskTag> tags = new ArrayList<>();
    tags.add(new TaskTag("Random1L", "tag1"));

    when(jdbcTemplate.batchUpdate(anyString(), any(SqlParameterSource[].class)))
        .thenThrow(new DataAccessException("DB error") {});

    // Act & Assert
    DataAccessException exception =
        assertThrows(
            DataAccessException.class,
            () -> {
              taskTagDao.createTaskTags(tags);
            });

    assertTrue(exception.getMessage().contains("DB error"));
    verify(jdbcTemplate).batchUpdate(anyString(), any(SqlParameterSource[].class));
  }

  @Test
  @DisplayName("createTaskTags: should return 0 when input list is empty")
  void testCreateTaskTagsWithEmptyList() {
    // Arrange
    ArrayList<TaskTag> emptyTags = new ArrayList<>();

    // Act
    int insertedCount = taskTagDao.createTaskTags(emptyTags);

    // Assert
    assertEquals(0, insertedCount, "Inserting empty list should return 0");
    // batchUpdate should NOT be called
    verify(jdbcTemplate, org.mockito.Mockito.never())
        .batchUpdate(anyString(), any(SqlParameterSource[].class));
  }
}
