package com.varun.vcommercestore.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varun.vcommercestore.Enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    //actuall inventory (not visible to user added by the vendor)
    private int quantity;

    //backend reservations handling
    private int reservedQuantity;

    //this will only be visible to user
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

    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns=@JoinColumn(name = "product_id",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id",nullable = false)
    )
    private Set<Category> categories = new HashSet<>();

    public int getAvailableQuantity() {
        return Math.max(0, quantity - reservedQuantity);
    }
}