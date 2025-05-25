package com.event.tasker.DAO.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.event.tasker.model.TeamMember;

@ExtendWith(MockitoExtension.class)
public class TeamMemberDaoImplTest {

  @Mock JdbcTemplate jdbcTemplate;

  @InjectMocks TeamMemberDaoImpl teamMemberDao;

  @Test
  @DisplayName("Unit Test: getTeamMembers Should return TeamMembers")
  void testGetTeamMembersShouldReturnTeamMembers() throws SQLException {
    // Given
    ResultSet mockResultSet = mock(ResultSet.class);
    // When
    when(mockResultSet.next()).thenReturn(true, true, false);
    when(mockResultSet.getString("id")).thenReturn("1", "2");
    when(mockResultSet.getString("name")).thenReturn("user1", "user2");

    when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class)))
        .thenAnswer(
            invocationOnMock -> {
              ResultSetExtractor<TeamMember> resultSetExtractor = invocationOnMock.getArgument(1);
              return resultSetExtractor.extractData(mockResultSet);
            });
    // Then
    ArrayList<TeamMember> teamMembers = teamMemberDao.getTeamMembers();

    assertNotNull(teamMembers, "TeamMembers should not be null");
    assertEquals(2, teamMembers.size(), "TeamMembers should have 2 members");

    verify(jdbcTemplate).query(anyString(), any(ResultSetExtractor.class));
  }

  @Test
  @DisplayName("Unit Test: getTeamMembers should throw exception and handle gracefully")
  void testGetTeamMembersShouldThrowExceptionAndHandleGracefully() {
    when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class)))
        .thenThrow(new DataAccessException("Simulated DB error") {});

    RuntimeException thrown =
        assertThrows(DataAccessException.class, () -> teamMemberDao.getTeamMembers());

    assertTrue(thrown.getMessage().contains("Error retrieving team members"));
    assertInstanceOf(DataAccessException.class, thrown.getCause());
  }
}
