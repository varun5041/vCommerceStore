package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.dtos.ProductDto;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import com.varun.vcommercestore.dtos.categoryDto;
import com.varun.vcommercestore.dtos.userDto;

import java.io.IOException;
import java.util.List;

public interface ProductServies {
    //create
    ProductDto createProduct(ProductDto productDto);
    //update
    ProductDto updateProdcut(ProductDto productDto,String ProductId);
    //delete
    void deleteProduct(String ProductId) throws IOException;
    //getbyid
    ProductDto getByid(String Product);
    //getallproducts
    PageResopnse<ProductDto> getAllProducts(int pagenumber, int pagesize,String sortby,String order);
    //filter

    //getalllive
    List<ProductDto> getallLiveProducts();
    //searchbyProduct
    List<ProductDto> searchProductByname(String keyword);
    //searchbyBrand
    List<ProductDto> searchByBrand(String brandKeyword);
    //search-global(anything)
    List<ProductDto> searchProducts(String keyword);

    //images serve and upload
    String saveProductImageName(String name,String ProductId);
    String getProductImageName(String ProductId);


}
