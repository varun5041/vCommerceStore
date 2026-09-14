package com.varun.vcommercestore.Exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GloabalExceptionHandler {
    //handler Resource Not Found Exception
    Logger logger = LoggerFactory.getLogger(GloabalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponce> ResourceNotFoundExceptionHandler(ResourceNotFoundException exception){
        logger.info("Expection Handler Invoked : ResourceNotFound");
        ExceptionResponce responce = ExceptionResponce.builder().message(exception.getMessage()).status(false).httpStatus(HttpStatus.NOT_FOUND).build();
        return new ResponseEntity<>(responce,HttpStatus.NOT_FOUND);
    }

}
