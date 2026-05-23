package com.codingwithmahesh.taskflow.taskflow_backend.controller;

import com.codingwithmahesh.taskflow.taskflow_backend.dto.common.MessageResponse;
import com.codingwithmahesh.taskflow.taskflow_backend.dto.task.TaskRequest;
import com.codingwithmahesh.taskflow.taskflow_backend.dto.task.TaskResponse;
import com.codingwithmahesh.taskflow.taskflow_backend.security.AuthenticatedUser;
import com.codingwithmahesh.taskflow.taskflow_backend.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
    @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        TaskResponse response = taskService.createTask(authenticatedUser.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(taskService.getTaskByUserId(authenticatedUser.getId()));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable @Positive Long taskId,
                                                @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(taskService.getTaskById(authenticatedUser.getId(), taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable @Positive Long taskId,
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        return ResponseEntity.ok(taskService.updateTask(authenticatedUser.getId(), taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<MessageResponse> deleteTask(
            @PathVariable @Positive Long taskId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        taskService.deleteTask(authenticatedUser.getId(), taskId);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
    }
}
