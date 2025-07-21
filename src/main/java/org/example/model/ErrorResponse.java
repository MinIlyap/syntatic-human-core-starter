package org.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String code, String message, LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }
}