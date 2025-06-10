package com.event.tasker.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.event.tasker.model.Task;
import com.event.tasker.model.TaskDetail;
import com.event.tasker.model.TaskerResponse;
import com.event.tasker.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

  private final TaskService taskService;

  @GetMapping("/list")
  public ResponseEntity<TaskerResponse<ArrayList<Task>>> getTasks() {
    try {
      ArrayList<Task> tasks = taskService.getTasks();
      if (tasks == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }

      TaskerResponse<ArrayList<Task>> response =
          TaskerResponse.<ArrayList<Task>>builder().data(tasks).build();

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error retrieving tasks", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{id}")
  public Object getTaskById(String id) {
    return new Object() {
      String test = "test";

      {
        Function<String, Integer> toLength = String::length;
        Function<Integer, String> intToString = Object::toString;
        Comparator<String> comparator =
            (s1, s2) -> {
              int result = Integer.compare(toLength.apply(s1), toLength.apply(s2));
              System.out.println("Comparing \"" + s1 + "\" with \"" + s2 + "\" = " + result);
              return result;
            };

        List<String> words = Arrays.asList("apple", "banana", "pear", "kiwi");

        final Function<Character, Character> key = t -> t;

        String apple = "apple";
        apple
            .chars()
            .mapToObj(i -> (char) i)
            .collect(Collectors.groupingBy(key, Collectors.counting()));

        Predicate<String> longerThanFive = word -> word.length() > 5;
        Consumer<String> print = System.out::println;

        words.stream().filter(longerThanFive).forEach(print);

        words.sort(comparator);
        System.out.println(words);
      }
    };
  }

  @PostMapping(consumes = {"multipart/form-data"})
  public Object createTask(
      @RequestPart TaskDetail task,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    return new Object() {};
  }

  @GetMapping("/{id}/start")
  public Object startTask(String id) {
    return new Object() {};
  }
}
