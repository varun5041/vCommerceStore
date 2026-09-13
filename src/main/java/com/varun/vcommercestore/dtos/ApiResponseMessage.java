package com.varun.vcommercestore.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseMessage {
    private String message;
    private HttpStatus httpStatus;
    private boolean success;
}
