package com.varun.vcommercestore.Exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class InvalidFileTypeException extends RuntimeException{
    private String message;

    public InvalidFileTypeException(String message){
        super(message);
    }

    public InvalidFileTypeException(){
        super("Invalid File Type");
    }

}
