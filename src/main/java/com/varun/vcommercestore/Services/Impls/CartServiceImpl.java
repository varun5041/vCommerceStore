package com.varun.vcommercestore.Services.Impls;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.*;
import com.varun.vcommercestore.Repositories.*;
import com.varun.vcommercestore.Services.CartService;
import com.varun.vcommercestore.dtos.Responcedtos.CartItemResponseDto;
import com.varun.vcommercestore.dtos.Responcedtos.CartResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    @Override
    @Transactional
    public void addtocart(String userid, String productid, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        //get productct
        Product product = productRepository.findById(productid)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found!"));

        //check if entered quanntity is > available in stock
        if (product.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        User user = userRepository.findById(userid)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found!"));

        Cart cart = user.getCart();

        CartItems existingItem =
                cartItemRepository.findByCartAndProduct(cart, product);

        int alreadyInCart = 0;
        if (existingItem != null) {
            alreadyInCart = existingItem.getQuantity();
        }

        // only a check, nothing is locked here
        if (product.getAvailableQuantity() < alreadyInCart + quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        if (existingItem != null) {
            existingItem.setQuantity(alreadyInCart + quantity);
            existingItem.setUpdatedate(LocalDateTime.now());
        } else {
            CartItems item = CartItems.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(quantity)
                    .addedDate(LocalDateTime.now())
                    .updatedate(LocalDateTime.now())
                    .build();
            cartItemRepository.save(item);
        }

        cart.setTotalItems(cart.getTotalItems() + quantity);
        cart.setTotalprice(cart.getTotalprice() + product.getDiscountPrice().doubleValue() * quantity);
        cartRepository.save(cart);
    }

    @Transactional
    @Override
    public void removefromcart(String userid,String productid,int quantity){
        User user = userRepository.findById(userid).orElseThrow(()->new ResourceNotFoundException("user not found!"));
        Cart cart= user.getCart();
        Product product = productRepository.findById(productid)
                .orElseThrow(()->new ResourceNotFoundException("product no longer in store"));

        CartItems item = cartItemRepository.findByCartAndProduct(cart,product);


        if(item == null){
            throw new ResourceNotFoundException("item not In Cart");
        }

        if(item.getQuantity()<quantity){
            throw new IllegalArgumentException("quantity is greater than in the cart");
        } else if (item.getQuantity()==quantity) {
            //nothing left
            cart.getCartItems().remove(item);
        }else{
            item.setQuantity(item.getQuantity() - quantity);
            item.setUpdatedate(LocalDateTime.now());
        }

        cart.setTotalItems(cart.getTotalItems()-quantity);
        cart.setTotalprice(cart.getTotalprice() - product.getDiscountPrice().doubleValue() * quantity);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartResponseDto getCart(String userid) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("user not found!"));

        Cart cart = user.getCart();
        List<CartItemResponseDto> items = new ArrayList<>();

        for (CartItems item : cart.getCartItems()) {
            Product product = item.getProduct();
            CartItemResponseDto dto = new CartItemResponseDto();
            dto.setProductid(product.getProductid());
            dto.setProductname(product.getProductname());
            dto.setProductImage(product.getProductImage());
            dto.setDiscountPrice(product.getDiscountPrice());
            dto.setQuantity(item.getQuantity());
            items.add(dto);
        }

        CartResponseDto response = new CartResponseDto();
        response.setCartId(cart.getCartId());
        response.setTotalItems(cart.getTotalItems());
        response.setTotalprice(cart.getTotalprice());
        response.setItems(items);
        return response;
    }
}
