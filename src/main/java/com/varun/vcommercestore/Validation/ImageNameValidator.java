package com.varun.vcommercestore.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageNameValidator implements ConstraintValidator<ImageNameValid,String> {
    Logger logger = LoggerFactory.getLogger(ImageNameValidator.class);
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        logger.info("Image Name validator Invoked!{}",s);
        //logic
        if(s.isBlank()){
            return false;
        }else{
            return true;
        }
    }
}
