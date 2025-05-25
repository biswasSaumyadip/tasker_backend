package com.event.tasker.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UiOptionTest {

  @Test
  @DisplayName("Unit Test: Builder should create UiOption with correct values")
  void testBuilderCreatesUiOption() {
    // Arrange & Act
    UiOption option = UiOption.builder().label("High Priority").value("HIGH").build();

    // Assert
    assertEquals("High Priority", option.getLabel());
    assertEquals("HIGH", option.getValue());
  }

  @Test
  @DisplayName("Unit Test: AllArgsConstructor should set fields correctly")
  void testAllArgsConstructor() {
    // Act
    UiOption option = new UiOption("Low Priority", "LOW");

    // Assert
    assertEquals("Low Priority", option.getLabel());
    assertEquals("LOW", option.getValue());
  }

  @Test
  @DisplayName("Unit Test: NoArgsConstructor with setters should work correctly")
  void testNoArgsConstructorWithSetters() {
    // Arrange
    UiOption option = new UiOption();

    // Act
    option.setLabel("Medium Priority");
    option.setValue("MEDIUM");

    // Assert
    assertEquals("Medium Priority", option.getLabel());
    assertEquals("MEDIUM", option.getValue());
  }

  @Test
  @DisplayName("Unit Test: equals and hashCode should behave as expected")
  void testEqualsAndHashCode() {
    UiOption option1 = new UiOption("label", "value");
    UiOption option2 = new UiOption("label", "value");
    UiOption option3 = new UiOption("labelX", "valueX");

    assertEquals(option1, option2);
    assertEquals(option1.hashCode(), option2.hashCode());
    assertNotEquals(option1, option3);
  }
}
