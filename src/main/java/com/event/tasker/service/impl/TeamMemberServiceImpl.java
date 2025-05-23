package com.event.tasker.service.impl;

import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.event.tasker.DAO.TeamMemberDao;
import com.event.tasker.model.TeamMember;
import com.event.tasker.service.TeamMemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberServiceImpl implements TeamMemberService {

  private final TeamMemberDao teamMemberDao;

  @Override
  public ArrayList<TeamMember> getTeamMembers() {
    try {
      return teamMemberDao.getTeamMembers();
    } catch (DataAccessException e) {
      log.error(e.getMessage());
      throw new DataAccessException(e.getMessage()) {
        @Override
        public String getMessage() {
          return super.getMessage();
        }
      };
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }
}
