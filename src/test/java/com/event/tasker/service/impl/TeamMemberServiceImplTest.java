package com.event.tasker.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import com.event.tasker.DAO.TeamMemberDao;
import com.event.tasker.model.TeamMember;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceImplTest {

  @Mock TeamMemberDao teamMemberDao;

  @InjectMocks TeamMemberServiceImpl teamMemberService;

  @Test
  @DisplayName("Unit Test: getTeamMembers Should get list of team members")
  void testGetTeamMembers_ShouldGetListOfTeamMembers() {
    // arrange
    ArrayList<TeamMember> teamMembers = new ArrayList<>();
    TeamMember teamMember1 = new TeamMember();
    TeamMember teamMember2 = new TeamMember();
    teamMembers.add(teamMember1);
    teamMembers.add(teamMember2);
    when(teamMemberDao.getTeamMembers()).thenReturn(teamMembers);

    // act
    ArrayList<TeamMember> result = teamMemberService.getTeamMembers();
    // assert
    assertTrue(result.size() == teamMembers.size());
  }

  @Test
  @DisplayName("Unit Test: getTeamMembers should throw exception and handle gracefully")
  void testGetTeamMembers_ShouldThrowExceptionAndHandleGracefully() {
    // arrange

    // act
    when(teamMemberDao.getTeamMembers()).thenThrow(new DataAccessException("DB error") {});

    // assert
    RuntimeException thrown =
        assertThrows(DataAccessException.class, () -> teamMemberService.getTeamMembers());

    assertTrue(thrown.getMessage().contains("DB error"));
  }

  @Test
  @DisplayName("Unit Test: getTeamMembers: Should handle unexpected exception gracefully")
  void testGetTeamMembers_ShouldHandleUnexpectedExceptionGracefully() {
    when(teamMemberDao.getTeamMembers()).thenThrow(new RuntimeException("DB error") {});

    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> teamMemberService.getTeamMembers());

    assertTrue(thrown.getMessage().contains("DB error"));
  }
}
