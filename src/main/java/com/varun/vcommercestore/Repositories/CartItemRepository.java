package com.varun.vcommercestore.Repositories;

import com.varun.vcommercestore.Models.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItems,String> {
}
