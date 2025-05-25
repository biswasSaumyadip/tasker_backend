package com.event.tasker.DAO.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.event.tasker.model.UiOption;

@ExtendWith(MockitoExtension.class)
public class UiOptionDaoTest {

  @InjectMocks UiOptionDaoImpl uiOptionDao;

  @Mock JdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("Unit Test: getPriorityLabels should get dropdown labels using id")
  void testGetPriorityLabels() throws SQLException {
    // Arrange
    ArrayList<UiOption> uiOptions = new ArrayList<>();
    UiOption uiOption = UiOption.builder().label("label").value("value").build();
    UiOption uiOption2 = UiOption.builder().label("label2").value("value2").build();
    uiOptions.add(uiOption);
    uiOptions.add(uiOption2);

    ResultSet rs = mock(ResultSet.class);

    when(rs.next()).thenReturn(true, true, false);

    when(rs.getString("label")).thenReturn("label", "label2");
    when(rs.getString("value")).thenReturn("value", "value2");

    when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class)))
        .thenAnswer(
            invocationOnMock -> {
              ResultSetExtractor<ArrayList<UiOption>> resultSetExtractor =
                  invocationOnMock.getArgument(1);
              return resultSetExtractor.extractData(rs);
            });

    // Act
    ArrayList<UiOption> result = uiOptionDao.getPriorityLabels("1");

    // Assert
    assertNotNull(result, "Result should not be null");
    assertNotNull(result.get(0), "Result should not be null");
    assertEquals(result.size(), 2, "Result should have 2 options");
    verify(jdbcTemplate).query(anyString(), any(ResultSetExtractor.class));
  }

  @Test
  @DisplayName("Unit Test: getPriorityLabels should throw exception and handle gracefully")
  void testGetPriorityLabels_ShouldThrowExceptionAndHandleGracefully() {
    when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class)))
        .thenThrow(new DataAccessException("Simulated DB error") {});

    RuntimeException exception =
        assertThrows(DataAccessException.class, () -> uiOptionDao.getPriorityLabels("1"));

    assertTrue(exception.getMessage().contains("Error retrieving priority labels"));
    verify(jdbcTemplate).query(anyString(), any(ResultSetExtractor.class));
  }
}
