package com.tech.techhubbackend.exceptionhandling.advice;

import com.tech.techhubbackend.exceptionhandling.exceptions.EntityAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ControllerAdvice
public class AppAdvice extends ResponseEntityExceptionHandler {

    @ResponseBody
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> entityAlreadyExistsExceptionHandler(EntityAlreadyExistsException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", e.getMessage());
        body.put("status", HttpStatus.CONFLICT);

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        body.put("time", formattedTime);

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}
