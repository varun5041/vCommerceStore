package com.varun.vcommercestore.Exceptions;

import jakarta.websocket.OnClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GloabalExceptionHandler {
    //handler Resource Not Found Exception
    Logger logger = LoggerFactory.getLogger(GloabalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponce> ResourceNotFoundExceptionHandler(ResourceNotFoundException exception){
        logger.info("Expection Handler Invoked : ResourceNotFound");
        ExceptionResponce responce = ExceptionResponce.builder()
                .message(exception.getMessage())
                .status(false)
                .httpStatus(HttpStatus.NOT_FOUND).build();
        return new ResponseEntity<>(responce,HttpStatus.NOT_FOUND);
    }

    //Method Argument not valid exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> MethodArgNotValidExceptionHandler(MethodArgumentNotValidException ex){
        logger.info("EXCEPTION HANLDER INVOKED! : MethodArgumentNotValidException");

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String,Object> response = new HashMap<>();
        fieldErrors.stream().forEach(fieldError -> {
            String field = fieldError.getField();
            String defaultMessage = fieldError.getDefaultMessage();
            response.put(field,defaultMessage);
        });

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> DataIntegrityViolationException(DataIntegrityViolationException ex){
        logger.info("Exception Triggered : Data Integrity Violation error");
        Map<String,Object> response = new HashMap<>();
        response.put("status", false);
        response.put("message", ex.getMessage());
        response.put("httpStatus", HttpStatus.CONFLICT);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}
