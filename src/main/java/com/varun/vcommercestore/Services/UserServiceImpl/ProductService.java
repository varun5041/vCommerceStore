package com.varun.vcommercestore.Services.UserServiceImpl;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.Category;
import com.varun.vcommercestore.Models.Product;
import com.varun.vcommercestore.Repositories.ProductRepository;
import com.varun.vcommercestore.Services.FileService;
import com.varun.vcommercestore.Services.ProductServies;
import com.varun.vcommercestore.Utils.Helper;
import com.varun.vcommercestore.dtos.ProductDto;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import com.varun.vcommercestore.dtos.categoryDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService implements ProductServies {

    @Autowired
    private Helper helper;
    @Autowired
    private ProductRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String ProductImagePath;

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Override
    public ProductDto createProduct(ProductDto productDto) {

        productDto.setProductid(UUID.randomUUID().toString());
        LocalDateTime current = LocalDateTime.now();
        productDto.setAddedDate(current);
        productDto.setUpdateDate(current);
        if (productDto.getProductImage() == null ||
                productDto.getProductImage().isEmpty()) {
            productDto.setProductImage("defproductimage.jpg");
        }
        Product product = dtotoEntity(productDto);

        Product savedProduct = repository.save(product);
        return entityToDto(savedProduct);
    }

    @Override
    public ProductDto updateProdcut(ProductDto productDto, String ProductId) {

        Product product = repository.findById(ProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Item Not Found!"));

        product.setProductname(productDto.getProductname());
        product.setProductDescription(productDto.getProductDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscountPercentage(productDto.getDiscountPercentage());
        product.setDiscountPrice(productDto.getDiscountPrice());
        product.setAvailableQuantity(productDto.getAvailableQuantity());
        product.setProductStatus(productDto.getProductStatus());
        product.setUpdateDate(LocalDateTime.now());
        product.setLive(productDto.isLive());
        product.setOutOfStock(productDto.isOutOfStock());
        product.setCategories(helper.getCategoriesbyids(productDto.getCategories()));
        Product updatedProduct = repository.save(product);

        return entityToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(String ProductId) throws IOException {

        logger.info("Deleting product with id: {}", ProductId);

        Product product = repository.findById(ProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Item Not Found!"));

        logger.debug("Product found with id: {}", ProductId);

        String imageName = product.getProductImage();

        if(imageName != null && !imageName.isEmpty() && !imageName.equalsIgnoreCase("defproductimage.jpg")) {
            logger.info("Deleting product image: {}", imageName);
            fileService.deleteFile(ProductImagePath,imageName);
            logger.info("Product image deleted successfully: {}", imageName);
        }

        repository.delete(product);

        logger.info("Product deleted successfully with id: {}", ProductId);
    }

    @Override
    public ProductDto getByid(String Product) {
        Product product = repository.findById(Product)
                .orElseThrow(() -> new ResourceNotFoundException("Item Not Found!"));

        return entityToDto(product);
    }

    @Override
    public PageResopnse<ProductDto> getAllProducts(
            int pagenumber,
            int pagesize,
            String sortby,
            String order) {

        Sort sorted = order.equalsIgnoreCase("desc") ?
                Sort.by(sortby).descending() :
                Sort.by(sortby).ascending();

        Pageable pageable = PageRequest.of(pagenumber, pagesize, sorted);

        Page<Product> page = repository.findAll(pageable);

        return helper.getPageResponse(page, ProductDto.class);
    }

    public List<ProductDto> searchProducts(String keyword){
        List<Product> searchResult = repository.searchProducts(keyword);
        List<ProductDto> productDtoList = searchResult.stream()
                .map(this::entityToDto).collect(Collectors.toList());
        return productDtoList;
    }

    //----------------------------------------------
    //SPECIAL FIELD WISE SEARCHING
     //----------------------------------------------


    @Override
    public List<ProductDto> getallLiveProducts() {
        List<Product> liveProducts =repository.findByIsLiveTrue();
        List<ProductDto> liveProductsDto = liveProducts.stream().map(product -> entityToDto(product)).collect(Collectors.toList());
        return liveProductsDto;
    }

    @Override
    public List<ProductDto> searchProductByname(String keyword) {
        List<Product> foundResults = repository.findByProductnameContainingIgnoreCase(keyword);
        List<ProductDto>foundresultsDto=foundResults.stream()
                .map(foundResult ->entityToDto(foundResult)).collect(Collectors.toList());
        return foundresultsDto;
    }

    @Override
    public List<ProductDto> searchByBrand(String brandKeyword) {
        List<Product> foundResults = repository.findByBrandIgnoreCase(brandKeyword);
        List<ProductDto>foundresultsDto=foundResults.stream()
                .map(foundResult ->entityToDto(foundResult)).collect(Collectors.toList());
        return foundresultsDto;
    }

    //--------------------------------------
    //IMAGE RELATED APIS
    //--------------------------------------

    @Override
    public String saveProductImageName(String name,String ProductId) {
        Product product = repository.findById(ProductId)
                .orElseThrow(()->new ResourceNotFoundException("Product not Found!"));
        product.setProductImage(name);
        repository.save(product);
        return name;
    }



    @Override
    public String getProductImageName(String ProductId) {
        Product product = repository.findById(ProductId)
                .orElseThrow(()->new ResourceNotFoundException("Product Does Not Exist"));
        String name = product.getProductImage();

        if(name == null || name.isEmpty()){
            name="defproductimage.jpg";
        }

        return name;
    }

    public Product dtotoEntity(ProductDto dto){

        logger.debug("Converting product DTO to entity");

        Product product = mapper.map(dto,Product.class);
        product.setCategories(helper.getCategoriesbyids(dto.getCategories()));

        return product;
    }

    public ProductDto entityToDto(Product product){

        logger.debug("Converting product entity to DTO");

        ProductDto productDto = mapper.map(product, ProductDto.class);
        productDto.setCategories(product.getCategories()
                .stream().map(c->c.getCategoryId()).collect(Collectors.toSet()));

        return productDto;
    }


}