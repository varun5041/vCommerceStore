package com.varun.vcommercestore.dtos.Responcedtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {
    private String productid;
    private String productname;
    private String productImage;
    private BigDecimal discountPrice;
    private int quantity;
}