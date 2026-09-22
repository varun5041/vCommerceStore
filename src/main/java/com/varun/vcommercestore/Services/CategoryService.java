package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.dtos.Requestdtos.Vendor.categoryDto;
import com.varun.vcommercestore.dtos.Responcedtos.CategoryResponseDto;
import com.varun.vcommercestore.dtos.Responcedtos.ProductResponseDto;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;

import java.io.IOException;
import java.util.List;

public interface CategoryService {
    //create
    CategoryResponseDto createCategory(categoryDto categoryRequest);

    //update
    CategoryResponseDto updateCategory(categoryDto categoryRequest, String categoryId);

    //delete
    void deleteCategory(String categoryId) throws IOException;

    //getall
    PageResopnse<CategoryResponseDto> getAllCategories(int pagenumber, int pagesize, String sortby, String order);

    //getsinglebyid
    CategoryResponseDto getCategoryById(String categoryId);

    String savecategoryImageName(String name, String categoryid);

    String getCategoryImageName(String categoryid);

    List<ProductResponseDto> getProductFromCategory(String CategoryId);
}