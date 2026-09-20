package com.varun.vcommercestore.dtos.Responcedtos;

import com.varun.vcommercestore.Enums.ProductStatus;
import com.varun.vcommercestore.Models.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDto {
    private String productid;
    private String productname;
    private String productDescription;
    private double price;
    private double discountPercentage;
    private int availableQuantity;
    private ProductStatus productStatus;
    private LocalDateTime addedDate;
    private LocalDateTime updateDate;
    private boolean isLive;
    private boolean outOfStock;
    private String productImage;
    private String brand;
    private BigDecimal discountPrice;
    private Set<String> categories;
}
