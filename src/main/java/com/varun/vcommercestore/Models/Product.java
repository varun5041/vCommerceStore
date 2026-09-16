package com.varun.vcommercestore.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varun.vcommercestore.Enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="Products")
public class Product {
    @Id
    private String productid;

    @Column(nullable = false)
    private String productname;

    @Column(length = 10000)
    private String productDescription;

    private double price;

    private double discountPercentage;

    private int availableQuantity;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private LocalDateTime addedDate;

    private LocalDateTime updateDate;

    private boolean isLive;

    private boolean outOfStock;

    private String productImage;

    private String brand;

    private BigDecimal discountPrice;
}