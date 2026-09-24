package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.dtos.Responcedtos.CartResponseDto;

public interface CartService {
    void addtocart(String userid,String productid,int quantity);

    void removefromcart(String userid,String productid,int quantity);

    CartResponseDto getCart(String userid);
}
