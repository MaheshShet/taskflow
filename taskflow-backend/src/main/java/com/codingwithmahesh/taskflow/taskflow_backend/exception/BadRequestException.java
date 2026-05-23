package com.codingwithmahesh.taskflow.taskflow_backend.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
