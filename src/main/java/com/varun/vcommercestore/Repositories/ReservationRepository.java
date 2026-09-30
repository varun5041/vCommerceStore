package com.varun.vcommercestore.Repositories;

import com.varun.vcommercestore.Models.Cart;
import com.varun.vcommercestore.Models.Product;
import com.varun.vcommercestore.Models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,String>{
    Reservation findByCartAndProduct(Cart cart, Product product);
    List<Reservation> findByCart(Cart cart);
    List<Reservation> findByExpiresAtBefore(LocalDateTime localDateTime);
}
