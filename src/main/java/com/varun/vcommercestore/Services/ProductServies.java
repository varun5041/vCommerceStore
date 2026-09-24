package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.dtos.Requestdtos.ProductRequestDto;
import com.varun.vcommercestore.dtos.Responcedtos.ProductResponseDto;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import com.varun.vcommercestore.dtos.UpdateRequestDto.ProductUpdateRequestDto;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ProductServies {
    //create
    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    //update
    ProductResponseDto updateProdcut(ProductUpdateRequestDto productRequestDto, String ProductId);

    //delete
    void deleteProduct(String ProductId) throws IOException;

    //getbyid
    ProductResponseDto getByid(String Product);

    //getallproducts
    PageResopnse<ProductResponseDto> getAllProducts(int pagenumber, int pagesize, String sortby, String order);

    //getalllive
    List<ProductResponseDto> getallLiveProducts();

    //searchbyProduct
    List<ProductResponseDto> searchProductByname(String keyword);

    //searchbyBrand
    List<ProductResponseDto> searchByBrand(String brandKeyword);

    //search-global(anything)
    List<ProductResponseDto> searchProducts(String keyword,String brand,Double minprice,Double maxprice,String categoryid);

    //images serve and upload
    String saveProductImageName(String name, String ProductId);

    String getProductImageName(String ProductId);

    //updateproductcategory
    ProductResponseDto updateProductCategory(String productid, Set<String> catids);
}