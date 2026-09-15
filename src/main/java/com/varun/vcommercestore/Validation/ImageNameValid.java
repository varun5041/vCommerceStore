package com.varun.vcommercestore.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
//this is a custom made validation annotation
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ImageNameValidator.class)
public @interface ImageNameValid {
    //error message
    String message() default "INVALID IMAGE NAME!!";

    //represent group of constraints
    Class<?>[] groups() default {};

    //additional info
    Class<? extends Payload>[] payload() default {};
}
