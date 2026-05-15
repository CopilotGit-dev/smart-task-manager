package com.kizora.taskmanager.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
//@AllArgsConstructor
public class ApiError {
    private int status;
    private String error;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private String path;
    
	public ApiError(int status, String error, String message, LocalDateTime timestamp, String path) {
		super();
		this.status = status;
		this.error = error;
		this.message = message;
		this.timestamp = timestamp;
		this.path = path;
	}
    
    
}
