package com.event.tasker.DAO.impl;

import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.event.tasker.DAO.UIOptionDao;
import com.event.tasker.model.UiOption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UiOptionDaoImpl implements UIOptionDao {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public ArrayList<UiOption> getPriorityLabels(String id) {
    try {
      String sql = """
				SELECT label, value
				FROM priority
				WHERE teamId = ?
				""";
      return jdbcTemplate.query(
          sql,
          rs -> {
            ArrayList<UiOption> uiOptions = new ArrayList<>();
            while (rs.next()) {
              UiOption uiOption =
                  UiOption.builder()
                      .label(rs.getString("label"))
                      .value(rs.getString("value"))
                      .build();
              uiOptions.add(uiOption);
            }
            return uiOptions;
          });
    } catch (DataAccessException e) {
      log.error("Error retrieving priority labels", e);
      throw new DataAccessException("Error retrieving priority labels") {};
    } catch (Exception e) {
      log.error("Error retrieving priority labels", e);
      throw new RuntimeException("Error retrieving priority labels");
    }
  }
}
