package com.varun.vcommercestore.Services.Impls;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.*;
import com.varun.vcommercestore.Repositories.*;
import com.varun.vcommercestore.Services.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.time.LocalDateTime;
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

        Product product = productRepository.findById(productid)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found!"));

        if (product.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        User user = userRepository.findById(userid)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found!"));

        Cart cart = user.getCart();

        CartItems existingItem =
                cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem != null) {

            existingItem.setQuantity(
                    existingItem.getQuantity() + quantity
            );

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

        cart.setTotalItems(
                cart.getTotalItems() + quantity
        );

        cart.setTotalprice(
                cart.getTotalprice()
                        + (product.getDiscountPrice().doubleValue() * quantity)
        );

        cartRepository.save(cart);

        Reservation existingReservation =
                reservationRepository.findByCartAndProduct(cart, product);

        if (existingReservation != null) {

            existingReservation.setQuantity(
                    existingReservation.getQuantity() + quantity
            );

        } else {

            Reservation newReservation = Reservation.builder()
                    .product(product)
                    .reservedAt(LocalDateTime.now())
                    .quantity(quantity)
                    .cart(cart)
                    .build();

            reservationRepository.save(newReservation);
        }

        product.setReservedQuantity(
                product.getReservedQuantity() + quantity
        );

        product.setAvailableQuantity(
                product.getQuantity()- product.getReservedQuantity()
        );
    }
}
