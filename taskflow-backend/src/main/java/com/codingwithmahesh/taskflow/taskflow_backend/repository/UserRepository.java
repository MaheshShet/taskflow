package com.codingwithmahesh.taskflow.taskflow_backend.repository;

import com.codingwithmahesh.taskflow.taskflow_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
}
