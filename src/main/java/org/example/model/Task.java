package org.example.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Task {
    @NotBlank
    @Size(max = 1000, message = "Description must be up to 1000 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotBlank
    @Size(max = 100, message = "Author must be up to 100 characters")
    private String author;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z", message = "Time must be in ISO8601 format")
    private String time;
}
