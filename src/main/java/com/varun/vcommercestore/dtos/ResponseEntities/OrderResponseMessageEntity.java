package com.varun.vcommercestore.dtos.ResponseEntities;

import com.varun.vcommercestore.dtos.Requestdtos.OrderRequestDto;
import com.varun.vcommercestore.dtos.Responcedtos.OrderResponseDto;
import lombok.*;
import org.springframework.http.HttpStatus;
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseMessageEntity {
    private OrderResponseDto orderResponseDto;
    private String message;
    private HttpStatus httpStatus;
    private boolean success;
}
