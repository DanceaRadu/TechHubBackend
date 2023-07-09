package com.tech.techhubbackend.exceptionhandling.advice;

import com.tech.techhubbackend.exceptionhandling.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ControllerAdvice
public class AppAdvice extends ResponseEntityExceptionHandler {

    private String getCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentTime.format(formatter);
    }

    @ResponseBody
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> entityAlreadyExistsExceptionHandler(EntityAlreadyExistsException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", e.getMessage());
        body.put("status", HttpStatus.CONFLICT);
        body.put("time", getCurrentTime());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ResponseBody
    @ExceptionHandler({ImageNotFoundException.class, UserNotFoundException.class, ProductNotFoundException.class, ShoppingCartEntryNotFoundException.class})
    public ResponseEntity<Object> imageNotFoundExceptionHandler(ImageNotFoundException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", e.getMessage());
        body.put("status", HttpStatus.NOT_FOUND);
        body.put("time", getCurrentTime());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @ExceptionHandler(ForbiddenRequestException.class)
    public ResponseEntity<Object> forbiddenRequestExceptionHandler(ForbiddenRequestException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", e.getMessage());
        body.put("status", HttpStatus.FORBIDDEN);
        body.put("time", getCurrentTime());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}
