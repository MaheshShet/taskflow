package com.codingwithmahesh.taskflow.taskflow_backend.repository;

import com.codingwithmahesh.taskflow.taskflow_backend.entity.Task;
import com.codingwithmahesh.taskflow.taskflow_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

}
