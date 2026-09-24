package com.varun.vcommercestore.dtos.Responcedtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {
    private String cartId;
    private int totalItems;
    private double totalprice;
    private List<CartItemResponseDto> items;
}
