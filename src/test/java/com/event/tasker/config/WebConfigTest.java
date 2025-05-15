package com.event.tasker.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Unit Test: WebConfig")
class WebConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("CORS configuration allows requests from valid origins")
  void testCorsConfiguration() throws Exception {
    mockMvc
        .perform(
            options("/api/task/list")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
        .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"))
        .andExpect(header().string("Access-Control-Expose-Headers", "Access-Control-Allow-Headers"))
        .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
  }

  @Test
  @DisplayName("CORS configuration blocks requests from invalid origins")
  void testCorsConfigurationWithInvalidOrigin() throws Exception {
    mockMvc
        .perform(
            options("/api/task/list")
                .header("Origin", "http://unauthorized-domain.com")
                .header("Access-Control-Request-Method", "GET"))
        .andExpect(status().isForbidden());
  }
}
