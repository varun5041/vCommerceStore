package com.varun.vcommercestore.Controllers;

import com.varun.vcommercestore.Services.CartService;
import com.varun.vcommercestore.dtos.ResponseEntities.ApiResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    CartService cartService;

    @PostMapping("/{userid}/cart/{productid}")
    public ResponseEntity<ApiResponseMessage> addtoCart(
            @RequestBody int quantity,
            @PathVariable("userid") String userid,
            @PathVariable("productid") String productid
    ){
        cartService.addtocart(userid, productid, quantity);
        ApiResponseMessage message =ApiResponseMessage.builder().message("Product added to cart with quantity = "+ quantity)
                .success(true)
                .httpStatus(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(message,HttpStatus.OK);
    }

}
