package com.logicarchive.dto.response;

import java.time.LocalDateTime;

public record ApiResponse(
        boolean success,
        String message,
        Object data,
        LocalDateTime timestamp
) {

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data, LocalDateTime.now());
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null, LocalDateTime.now());
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null, LocalDateTime.now());
    }

    public static ApiResponse error(String message, Object data) {
        return new ApiResponse(false, message, data, LocalDateTime.now());
    }
}
