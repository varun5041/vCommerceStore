package com.varun.vcommercestore.Controllers;

import com.varun.vcommercestore.Services.OrderServices;
import com.varun.vcommercestore.dtos.Requestdtos.OrderRequestDto;
import com.varun.vcommercestore.dtos.Responcedtos.OrderResponseDto;
import com.varun.vcommercestore.dtos.Responcedtos.ProductResponseDto;
import com.varun.vcommercestore.dtos.ResponseEntities.ApiResponseMessage;
import com.varun.vcommercestore.dtos.ResponseEntities.OrderResponseMessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    OrderServices orderService;

    @PostMapping("/placeorder/{userid}")
    public ResponseEntity<OrderResponseMessageEntity> confirOrder(
            @PathVariable String userid,
            @RequestBody OrderRequestDto dto
            ){
        OrderResponseDto orderResponseDto = orderService.CreateOrder(userid, dto);
        OrderResponseMessageEntity message = OrderResponseMessageEntity.builder().message("Order placed Successfully!")
                .httpStatus(HttpStatus.CREATED)
                .success(true)
                .orderResponseDto(orderResponseDto).build();
        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }
}
