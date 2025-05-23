package com.event.tasker.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.event.tasker.model.TeamMember;
import com.event.tasker.service.TeamMemberService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: TeamMemberController")
class TeamMemberControllerTest {

  @Mock private TeamMemberService teamMemberService;

  @InjectMocks private TeamMemberController teamMemberController;

  @Test
  @DisplayName("Get team members returns successful response with team member list")
  void testGetTeamMembers_Success() {
    // Given
    ArrayList<TeamMember> mockTeamMembers = new ArrayList<>();
    mockTeamMembers.add(new TeamMember());
    when(teamMemberService.getTeamMembers()).thenReturn(mockTeamMembers);

    // When
    ResponseEntity<ArrayList<TeamMember>> response = teamMemberController.getTeamMembers();

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode(), "Should return OK status");
    assertEquals(mockTeamMembers, response.getBody(), "Should return team members from service");
  }

  @Test
  @DisplayName("Get team members returns error when service returns null")
  void testGetTeamMembers_NullResponse() {
    // Given
    when(teamMemberService.getTeamMembers()).thenReturn(null);

    // When
    ResponseEntity<ArrayList<TeamMember>> response = teamMemberController.getTeamMembers();

    // Then
    assertEquals(
        HttpStatus.INTERNAL_SERVER_ERROR,
        response.getStatusCode(),
        "Should return INTERNAL_SERVER_ERROR when service returns null");
  }

  @Test
  @DisplayName("Get team members returns error when service throws exception")
  void testGetTeamMembers_Exception() {
    // Given
    when(teamMemberService.getTeamMembers()).thenThrow(new RuntimeException("Test exception"));

    // When
    ResponseEntity<ArrayList<TeamMember>> response = teamMemberController.getTeamMembers();

    // Then
    assertEquals(
        HttpStatus.INTERNAL_SERVER_ERROR,
        response.getStatusCode(),
        "Should return INTERNAL_SERVER_ERROR when service throws exception");
  }

  @Test
  @DisplayName("Controller setup initializes MockMvc correctly")
  void testControllerSetup() {
    // Given
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(teamMemberController).build();

    // Then
    assertNotNull(mockMvc, "MockMvc should be initialized");
  }
}
