package com.event.tasker.controller;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event.tasker.model.TeamMember;
import com.event.tasker.service.TeamMemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Slf4j
public class TeamMemberController {

  private final TeamMemberService teamMemberService;

  @GetMapping("/members")
  public ResponseEntity<ArrayList<TeamMember>> getTeamMembers() {
    try {
      log.info("Getting team members");
      ArrayList<TeamMember> teamMembers = teamMemberService.getTeamMembers();
      if (teamMembers == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
      return ResponseEntity.ok(teamMembers);
    } catch (Exception e) {
      log.error("Error retrieving team members", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
