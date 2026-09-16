package com.varun.vcommercestore.Repositories;

import com.varun.vcommercestore.Enums.ProductStatus;
import com.varun.vcommercestore.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,String> {
    //search by product name
    List<Product> findByProductnameContainingIgnoreCase(String keyword);

    List<Product> findByProductDescriptionContainingIgnoreCase(String keyword);

    List<Product> findByBrandContainingIgnoreCase(String keyword);

    // Find live products
    List<Product> findByIsLiveTrue();

    // Find products which are out of stock
    List<Product> findByOutOfStockTrue();

    // Find products which are in stock
    List<Product> findByOutOfStockFalse();

    // Find products by status
    List<Product> findByProductStatus(ProductStatus productStatus);

    // Price filtering
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    // Products below a price
    List<Product> findByPriceLessThanEqual(double price);

    // Products above a price
    List<Product> findByPriceGreaterThanEqual(double price);

    // Brand filtering
    List<Product> findByBrandIgnoreCase(String brand);
}
