package com.lelarn.dreamshops.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;
    private String error;

    // Constructor for success response
    private ApiResponse(HttpStatus status, String message, T data) {
        this.statusCode = status.value();
        this.message = message;
        this.data = data;
    }

    // Constructor for error response
    private ApiResponse(HttpStatus status, String message, String error) {
        this.statusCode = status.value();
        this.message = message;
        this.error = error;
    }

    // Static factory method for success response
    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message, data));
    }

    // Static factory method for success response with default OK status
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return success(HttpStatus.OK, message, data);
    }

    // Static factory method for success response with default OK status and message
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(HttpStatus.OK, "Success", data);
    }


    // Static factory method for error response
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, String errorDetails) {
        // Ensure data is null for error responses
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message, errorDetails));
    }

    // Static factory method for error response using ErrorResponse details
    public static <T> ResponseEntity<ApiResponse<T>> error(ErrorResponse errorResponse) {
        // Ensure data is null for error responses
        return ResponseEntity.status(errorResponse.getStatus())
                .body(new ApiResponse<>(HttpStatus.valueOf(errorResponse.getStatus()), errorResponse.getMessage(), errorResponse.getError()));
    }
}