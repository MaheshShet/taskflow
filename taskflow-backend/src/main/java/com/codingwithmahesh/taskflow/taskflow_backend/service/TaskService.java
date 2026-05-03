package com.codingwithmahesh.taskflow.taskflow_backend.service;

import com.codingwithmahesh.taskflow.taskflow_backend.dto.task.TaskRequest;
import com.codingwithmahesh.taskflow.taskflow_backend.dto.task.TaskResponse;
import com.codingwithmahesh.taskflow.taskflow_backend.entity.Task;
import com.codingwithmahesh.taskflow.taskflow_backend.entity.User;
import com.codingwithmahesh.taskflow.taskflow_backend.exception.ResourceNotFoundException;
import com.codingwithmahesh.taskflow.taskflow_backend.repository.TaskRepository;
import com.codingwithmahesh.taskflow.taskflow_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponse createTask(Long userId, TaskRequest taskRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found!"));

        Task task = new Task();
        task.setUser(user);
        mapToTask(task, taskRequest);

        Task savedTask = taskRepository.save(task);

        return  mapToTaskResponse(savedTask);
    }

    public List<TaskResponse> getTaskByUserId(Long userId) {
        List<TaskResponse> tasks = taskRepository.findByUserId(userId)
                .stream()
                .map(this::mapToTaskResponse)
                .toList();

        return tasks;
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long userId, Long taskId) {
       Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found!"));

       return mapToTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long userId, Long taskId, TaskRequest request) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
        mapToTask(task, request);

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Task not found!"));

        taskRepository.delete(task);
    }

    private void mapToTask(Task task, TaskRequest request) {
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
    }

    private TaskResponse mapToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        return response;
    }
}
