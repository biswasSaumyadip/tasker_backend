package com.event.tasker.service.impl;

import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.event.tasker.DAO.impl.UiOptionDaoImpl;
import com.event.tasker.model.UiOption;
import com.event.tasker.service.UiOptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UiOptionServiceImpl implements UiOptionService {
  private final UiOptionDaoImpl uiOptionDao;

  @Override
  public ArrayList<UiOption> getPriorityLabels(String id) {
    try {
      return uiOptionDao.getPriorityLabels(id);
    } catch (DataAccessException e) {
      log.error("Error retrieving priority labels", e);
      throw e;
    } catch (Exception e) {
      log.error("Error retrieving priority labels", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
