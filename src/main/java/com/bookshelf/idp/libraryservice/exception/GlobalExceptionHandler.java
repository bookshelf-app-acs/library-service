package com.bookshelf.idp.libraryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorObject> handleBadRequest(RuntimeException ex, WebRequest request) {
        ErrorObject error = new ErrorObject();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorObject> handleNotFound(RuntimeException ex, WebRequest request) {
        ErrorObject error = new ErrorObject();
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorObject> handleGeneral(RuntimeException ex, WebRequest request) {
        ErrorObject error = new ErrorObject();
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}