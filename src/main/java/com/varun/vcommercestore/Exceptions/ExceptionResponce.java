package com.varun.vcommercestore.Exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponce {
    private String message;
    private boolean status;
    private HttpStatus httpStatus;

}
