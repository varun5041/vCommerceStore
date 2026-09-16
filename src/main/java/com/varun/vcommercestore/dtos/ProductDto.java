package com.varun.vcommercestore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varun.vcommercestore.Enums.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private String productid;

    private String productname;

    private String productDescription;

    private double discountPercentage;

    private double price;

    private int availableQuantity;

    private ProductStatus productStatus;

    private LocalDateTime addedDate;

    private LocalDateTime updateDate;

    @JsonProperty("isLive")
    private boolean isLive;

    private boolean outOfStock;

    private String productImage;

    private String brand;

    private BigDecimal discountPrice;
}
