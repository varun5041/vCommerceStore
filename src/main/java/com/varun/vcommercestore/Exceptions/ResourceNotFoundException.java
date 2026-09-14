package com.varun.vcommercestore.Exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(){
        super("Resource Not Found");
    }

    public ResourceNotFoundException(String messsage){
        super(messsage);
    }
}
