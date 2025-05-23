package com.event.tasker.DAO.impl;

import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.event.tasker.DAO.TeamMemberDao;
import com.event.tasker.model.TeamMember;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TeamMemberDaoImpl implements TeamMemberDao {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public ArrayList<TeamMember> getTeamMembers() {
    String sql =
        """
				SELECT u.user_id id, CONCAT(u.first_name, ' ', u.last_name) AS name
				FROM users u""";

    try {
      return jdbcTemplate.query(
          sql,
          rs -> {
            ArrayList<TeamMember> teamMembers = new ArrayList<>();
            while (rs.next()) {
              TeamMember teamMember =
                  TeamMember.builder().id(rs.getString("id")).name(rs.getString("name")).build();

              teamMembers.add(teamMember);
            }
            return teamMembers;
          });
    } catch (Exception e) {
      log.error("Error retrieving team members", e);
      throw new DataAccessException("Error retrieving team members", e) {};
    }
  }
}
