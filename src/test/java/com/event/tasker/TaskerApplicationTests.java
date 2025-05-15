package com.event.tasker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
@DisplayName("Unit Test: TaskerApplication")
class TaskerApplicationTests {

  @Test
  @DisplayName("Spring context loads successfully")
  void contextLoads() {}

  @Test
  @DisplayName("Main method starts application correctly")
  void testMainMethod() {
    try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
      mocked
          .when(() -> SpringApplication.run(TaskerApplication.class, new String[] {}))
          .thenReturn(Mockito.mock(ConfigurableApplicationContext.class));

      TaskerApplication.main(new String[] {});

      mocked.verify(() -> SpringApplication.run(TaskerApplication.class, new String[] {}));
    }
  }
}
