package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.dtos.ProductDto;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import com.varun.vcommercestore.dtos.categoryDto;

import java.io.IOException;
import java.util.List;

public interface CategoryService {
    //create
    categoryDto createCategory(categoryDto categoryDto);

    //update
    categoryDto updateCategory(categoryDto categoryDto, String categoryId);

    //delete
    void deleteCategory(String categoryId) throws IOException;

    //getall
    PageResopnse<categoryDto> getAllCategories(int pagenumber, int pagesize, String sortby, String order);

    //getsinglebyid
    categoryDto getCategoryById(String categoryId);

    String savecategoryImageName(String name,String categoryid);

    String getCategoryImageName(String categoryid);

    List<ProductDto> getProductFromCategory(String CategoryId);

}