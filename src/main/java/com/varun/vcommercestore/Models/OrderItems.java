package com.varun.vcommercestore.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItems {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String OrderItemsId;

    private double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
