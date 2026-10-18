package com.clubdeportivo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard DTO representing detailed API error responses.
 */
@Getter
@Setter
@Builder
public class ErrorResponse {

    private int status;
    private String error;
    private String message;

    @Builder.Default
    private List<String> details = new ArrayList<>();

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructor sin argumentos
    public ErrorResponse() {
        this.details = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    // Constructores de conveniencia
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, List<String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details != null ? details : new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    // Constructor completo
    public ErrorResponse(int status, String error, String message, List<String> details, LocalDateTime timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details != null ? details : new ArrayList<>();
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }
}
