package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.dtos.Requestdtos.OrderRequestDto;
import com.varun.vcommercestore.dtos.Responcedtos.OrderResponseDto;

import java.util.List;

public interface OrderServices {
    OrderResponseDto CreateOrder(String userid, OrderRequestDto dto);
    List<OrderResponseDto> getOrdersByUser(String userid);
}
