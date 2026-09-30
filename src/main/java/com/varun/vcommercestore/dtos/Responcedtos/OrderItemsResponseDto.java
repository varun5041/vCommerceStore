package com.varun.vcommercestore.dtos.Responcedtos;

import com.varun.vcommercestore.Models.Order;
import com.varun.vcommercestore.Models.Product;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsResponseDto
{
    private String productid;
    private String productname;
    private String productImage;
    private double price;
    private int quantity;
}
