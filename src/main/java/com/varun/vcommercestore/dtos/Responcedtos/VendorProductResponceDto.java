package com.varun.vcommercestore.dtos.Responcedtos;

import com.varun.vcommercestore.Enums.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VendorProductResponceDto {
    private String productid;
    private String productname;
    private String productDescription;
    private double price;
    private double discountPercentage;
    private int quantity;
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
