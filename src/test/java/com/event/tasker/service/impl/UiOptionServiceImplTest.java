package com.event.tasker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.event.tasker.DAO.impl.UiOptionDaoImpl;
import com.event.tasker.model.UiOption;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: UiOptionServiceImpl")
public class UiOptionServiceImplTest {

  @InjectMocks UiOptionServiceImpl uiOptionService;

  @Mock UiOptionDaoImpl uiOptionDao;

  @Test
  @DisplayName("Unit Test: getPriorityLabels should get list of priority labels for dropdown")
  void testGetPriorityLabelsShouldGetListOfPriorityLabels() {
    // Arrange
    ArrayList<UiOption> uiOptions = new ArrayList<>();
    uiOptions.add(UiOption.builder().label("High").value("1").build());
    uiOptions.add(UiOption.builder().label("Low").value("2").build());

    when(uiOptionDao.getPriorityLabels("1")).thenReturn(uiOptions);

    // Act
    ArrayList<UiOption> result = uiOptionService.getPriorityLabels("1");

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("High", result.get(0).getLabel());
    assertEquals("1", result.get(0).getValue());
    verify(uiOptionDao).getPriorityLabels("1");
  }

  @Test
  @DisplayName("Unit Test: getPriorityLabels should throw DataAccessException and log error")
  void testGetPriorityLabelsShouldThrowExceptionAndHandleGracefully() {
    // Arrange
    DataAccessException dataAccessException = new DataAccessException("DB error") {};
    when(uiOptionDao.getPriorityLabels("1")).thenThrow(dataAccessException);

    // Act & Assert
    DataAccessException thrown =
        assertThrows(DataAccessException.class, () -> uiOptionService.getPriorityLabels("1"));

    assertEquals("DB error", thrown.getMessage());
    verify(uiOptionDao).getPriorityLabels("1");
  }

  @Test
  @DisplayName("Unit Test: getPriorityLabels should handle unexpected exceptions gracefully")
  void testGetPriorityLabelsShouldHandleUnexpectedExceptionGracefully() {
    // Arrange
    when(uiOptionDao.getPriorityLabels("1")).thenThrow(new RuntimeException("Unexpected error"));

    // Act & Assert
    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> uiOptionService.getPriorityLabels("1"));

    assertEquals("Unexpected error", thrown.getMessage());
    verify(uiOptionDao).getPriorityLabels("1");
  }
}
