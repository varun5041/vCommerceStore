package com.varun.vcommercestore.dtos.Responcedtos;

import com.varun.vcommercestore.Enums.PaymentMethods;
import com.varun.vcommercestore.Models.OrderItems;
import com.varun.vcommercestore.Models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto
{
    private String orderid;
    private double totalamount;
    private PaymentMethods PaymentMethod;
    private String OrderAddress;
    private String OrderName;
    private String orderPhoneNumber;
    private LocalDateTime orderdate;
    private List<OrderItemsResponseDto> orderItemsList;
}
