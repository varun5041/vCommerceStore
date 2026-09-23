package com.varun.vcommercestore.dtos.Requestdtos.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varun.vcommercestore.Enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 50, message = "Product name must be between 2 and 50 characters")
    private String productname;

    @Size(max = 10000, message = "Product description cannot exceed 10000 characters")
    private String productDescription;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "Discount percentage cannot exceed 100")
    private double discountPercentage;

    @PositiveOrZero(message = "Price cannot be negative")
    private double price;

    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private int quantity;

    private ProductStatus productStatus;

    @JsonProperty("isLive")
    private boolean isLive;

    @NotBlank(message = "Brand is required")
    @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters")
    private String brand;

    @NotEmpty(message = "At least one category is required")
    private Set<@NotBlank(message = "Category cannot be blank") String> categories = new HashSet<>();
}
