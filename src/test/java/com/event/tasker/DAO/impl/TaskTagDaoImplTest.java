package com.event.tasker.DAO.impl;

import static com.event.tasker.util.MockUtils.anyResultSetExtractor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.event.tasker.model.TaskTag;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TaskTagDaoImpl")
public class TaskTagDaoImplTest {

  @InjectMocks TaskTagDaoImpl taskTagDao; // your DAO class

  @Mock NamedParameterJdbcTemplate jdbcTemplate;

  @Mock Gson gson;

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
        assertThrows(DataAccessException.class, () -> taskTagDao.createTaskTags(tags));

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

  @Test
  @DisplayName("getTaskTagsBy: Should return ArrayList of tags")
  void testGetTaskTagsBy_ShouldReturnArrayListOfTags() {
    // Arrange
    ArrayList<TaskTag> tags = new ArrayList<>();
    tags.add(new TaskTag("Random1L", "tag1"));
    tags.add(new TaskTag("Random2L", "tag2"));

    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), anyResultSetExtractor()))
        .thenReturn(tags);

    // Act
    ArrayList<String> taskTags = taskTagDao.getTaskTagsBy("uuid1");

    // Assert
    assertEquals(2, taskTags.size(), "Incorrect number of task tags");
    verify(jdbcTemplate)
        .query(anyString(), any(MapSqlParameterSource.class), anyResultSetExtractor());
  }

  @Test
  @DisplayName("getTaskTagsBy: Should return empty ArrayList")
  void testGetTaskTagsBy_ShouldReturnEmptyArrayList() {
    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), anyResultSetExtractor()))
        .thenReturn(new ArrayList<>());

    ArrayList<String> taskTags = taskTagDao.getTaskTagsBy("uuid1");

    assertEquals(0, taskTags.size(), "Incorrect number of task tags");
    verify(jdbcTemplate)
        .query(anyString(), any(MapSqlParameterSource.class), anyResultSetExtractor());
  }

  @Test
  @DisplayName("getTaskTagsBy: Should throw JSONSyntaxException and handled gracefully")
  void testGetTaskTagsBy_ShouldThrowJSONSyntaxExceptionAndHandledGracefully() throws SQLException {
    ArrayList<TaskTag> tags = new ArrayList<>();

    tags.add(new TaskTag("Random1L", "tag1"));
    tags.add(new TaskTag("Random2L", "tag2"));

    ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenReturn(true).thenReturn(false);
    when(rs.getString("tags")).thenReturn("malformed-json");

    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), anyResultSetExtractor()))
        .thenAnswer(
            invMock -> {
              ResultSetExtractor<?> rse = invMock.getArgument(2);
              return rse.extractData(rs);
            });

    when(gson.fromJson(anyString(), any(Type.class)))
        .thenThrow(new JsonSyntaxException("mock malformed JSON"));

    // ACT
    ArrayList<String> taskTags = taskTagDao.getTaskTagsBy("uuid1");

    assertEquals(0, taskTags.size(), "Incorrect number of task tags");
    verify(gson).fromJson(anyString(), any(Type.class));
    verify(jdbcTemplate)
        .query(anyString(), any(MapSqlParameterSource.class), anyResultSetExtractor());
  }

  @Test
  @DisplayName("getTaskTagsBy: Should throw dataAccessException and Handle gracefully")
  void testGetTaskTagsBy_ShouldThrowDataAccessExceptionAndHandledGracefully() throws SQLException {
    when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), anyResultSetExtractor()))
        .thenThrow(new RecoverableDataAccessException("DB error"));

    assertThrowsExactly(
        RecoverableDataAccessException.class, () -> taskTagDao.getTaskTagsBy("uuid1"));
    verify(jdbcTemplate)
        .query(anyString(), any(MapSqlParameterSource.class), anyResultSetExtractor());
  }

  @Test
  @DisplayName("deleteTaskTags: Should soft delete and return number of rows affected")
  void testDeleteTaskTags_ShouldSoftDeleteAndReturnNumberOfRowsAffected() {

    ArrayList<TaskTag> tags = new ArrayList<>();
    tags.add(new TaskTag("Random1L", "tag1"));
    tags.add(new TaskTag("Random2L", "tag2"));

    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(2);

    int rowsAffected = taskTagDao.deleteTaskTags(tags, "uuid1");

    assertEquals(2, rowsAffected, "Two rows affected");
    verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  @DisplayName("deleteTaskTags: Should rethrow data access exception")
  void testDeleteTaskTags_ShouldRethrowDataAccessException() {
    ArrayList<TaskTag> tags = new ArrayList<>();
    tags.add(new TaskTag("Random1L", "tag1"));
    tags.add(new TaskTag("Random2L", "tag2"));
    when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class)))
        .thenThrow(new RecoverableDataAccessException("DB error"));

    assertThrowsExactly(
        RecoverableDataAccessException.class, () -> taskTagDao.deleteTaskTags(tags, "uuid1"));
    verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  @DisplayName("deleteTaskTags: Should handle empty or null values gracefully")
  void testDeleteTaskTags_ShouldHandleEmptyOrNullValuesGracefully() {
    ArrayList<TaskTag> tags = new ArrayList<>();

    taskTagDao.deleteTaskTags(tags, "uuid1");
    verifyNoInteractions(jdbcTemplate);
  }
}
