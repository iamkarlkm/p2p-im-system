package com.im.server.chatbot;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BotControllerAdvice {

    @ExceptionHandler(BotException.class)
    public ResponseEntity<Map<String, Object>> handleBotException(BotException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("error", ex.getMessage());
        error.put("errorCode", ex.getErrorCode());
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof BotException.BotNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof BotException.UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof BotException.BotDisabledException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof BotException.MaxBotsReachedException) {
            status = HttpStatus.CONFLICT;
        }
        
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("error", "Internal server error");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
