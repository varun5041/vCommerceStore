package com.varun.vcommercestore.Services.Impls;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.Category;
import com.varun.vcommercestore.Models.Product;
import com.varun.vcommercestore.Repositories.ProductRepository;
import com.varun.vcommercestore.Services.FileService;
import com.varun.vcommercestore.Services.ProductServies;
import com.varun.vcommercestore.Utils.Helper;
import com.varun.vcommercestore.dtos.Requestdtos.User.ProductRequestDto;
import com.varun.vcommercestore.dtos.Responcedtos.ProductResponseDto;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import com.varun.vcommercestore.dtos.UpdateRequestDto.ProductUpdateRequestDto;
import jakarta.transaction.Transactional;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductServies {

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

    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    // =========================
    // CREATE PRODUCT
    // =========================
    @Override
    public ProductResponseDto createProduct(ProductRequestDto request) {
        logger.info("Creating new product");
        Product product = new Product();
        // fields the server sets (client can't send these)
        product.setProductid(UUID.randomUUID().toString());
        LocalDateTime current = LocalDateTime.now();
        product.setAddedDate(current);
        product.setUpdateDate(current);
        product.setProductImage("defproductimage.jpg");
        // fields that come from the request
        copyRequestToProduct(request, product);
        Product savedProduct = repository.save(product);
        logger.info("Product created successfully with id: {}", savedProduct.getProductid());
        return entityToDto(savedProduct);
    }


    // =========================
    // UPDATE PRODUCT
    // =========================
    @Override
    @Transactional
    public ProductResponseDto updateProdcut(ProductUpdateRequestDto request, String ProductId) {
        logger.info("Updating product with id: {}", ProductId);
        Product product = repository.findById(ProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Item Not Found!"));
        copyUpdateRequestToProduct(request, product);
        product.setUpdateDate(LocalDateTime.now());
        Product updatedProduct = repository.save(product);
        logger.info("Product updated successfully with id: {}", ProductId);
        return entityToDto(updatedProduct);
    }

    // =========================
    // UPDATE PRODUCT CATEGORY
    // =========================
    @Override
    public ProductResponseDto updateProductCategory(String productid, Set<String> catids) {
        Product product = repository.findById(productid).orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
        Set<Category> categories = helper.getCategoriesbyids(catids);
        product.setCategories(categories);
        product.setUpdateDate(LocalDateTime.now());
        Product updatedProduct = repository.save(product);
        return entityToDto(updatedProduct);
    }

    // =========================
    // DELETE PRODUCT
    // =========================
    @Override
    public void deleteProduct(String ProductId) throws IOException {

        logger.info("Deleting product with id: {}", ProductId);

        Product product = repository.findById(ProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Item Not Found!"));

        logger.debug("Product found with id: {}", ProductId);

        String imageName = product.getProductImage();

        // delete the DB row first, then the image file
        repository.delete(product);

        if (imageName != null && !imageName.isEmpty() && !imageName.equalsIgnoreCase("defproductimage.jpg")) {
            logger.info("Deleting product image: {}", imageName);
            // (name, path) is the correct order
            fileService.deleteFile(imageName, ProductImagePath);
            logger.info("Product image deleted successfully: {}", imageName);
        }

        logger.info("Product deleted successfully with id: {}", ProductId);
    }


    // =========================
    // GET PRODUCT BY ID
    // =========================
    @Override
    public ProductResponseDto getByid(String Product) {
        Product product = repository.findById(Product)
                .orElseThrow(() -> new ResourceNotFoundException("Item Not Found!"));

        return entityToDto(product);
    }


    // =========================
    // GET ALL PRODUCTS (PAGED)
    // =========================
    @Override
    public PageResopnse<ProductResponseDto> getAllProducts(
            int pagenumber,
            int pagesize,
            String sortby,
            String order) {

        Sort sorted = order.equalsIgnoreCase("desc") ?
                Sort.by(sortby).descending() :
                Sort.by(sortby).ascending();

        Pageable pageable = PageRequest.of(pagenumber, pagesize, sorted);

        Page<Product> page = repository.findAll(pageable);

        // converted one by one so the categories come out as ids
        List<ProductResponseDto> content = page.getContent().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        return PageResopnse.<ProductResponseDto>builder()
                .content(content)
                .ppagenumber(page.getNumber())
                .pagesize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalpages(page.getTotalPages())
                .lastpage(page.isLast())
                .build();
    }


    // =========================
    // GLOBAL SEARCH
    // =========================
    @Override
    public List<ProductResponseDto> searchProducts(String keyword, String brand, Double minprice, Double maxprice, String categoryId) {
        List<Product> searchResult = repository.searchAndFilterProducts(keyword, brand, minprice, maxprice, categoryId);
        return searchResult.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }


    //----------------------------------------------
    //SPECIAL FIELD WISE SEARCHING
    //----------------------------------------------

    @Override
    public List<ProductResponseDto> getallLiveProducts() {
        List<Product> liveProducts = repository.findByIsLiveTrue();
        return liveProducts.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> searchProductByname(String keyword) {
        List<Product> foundResults = repository.findByProductnameContainingIgnoreCase(keyword);
        return foundResults.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> searchByBrand(String brandKeyword) {
        List<Product> foundResults = repository.findByBrandIgnoreCase(brandKeyword);
        return foundResults.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }


    //--------------------------------------
    //IMAGE RELATED APIS
    //--------------------------------------

    @Override
    public String saveProductImageName(String name, String ProductId) {
        Product product = repository.findById(ProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not Found!"));
        product.setProductImage(name);
        repository.save(product);
        return name;
    }

    @Override
    public String getProductImageName(String ProductId) {
        Product product = repository.findById(ProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Does Not Exist"));
        String name = product.getProductImage();

        if (name == null || name.isEmpty()) {
            name = "defproductimage.jpg";
        }

        return name;
    }


    //--------------------------------------
    //HELPER METHODS
    //--------------------------------------

    // Copies the fields the client is allowed to send onto the product.
    // Used by both create and update.
    private void copyRequestToProduct(ProductRequestDto request, Product product) {
        double discountAmount =
                request.getPrice() * request.getDiscountPercentage() / 100;

        product.setProductname(request.getProductname());
        product.setProductDescription(request.getProductDescription());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setQuantity(request.getQuantity());
        product.setProductStatus(request.getProductStatus());
        product.setLive(request.isLive());

        product.setDiscountPrice(
                BigDecimal.valueOf(request.getPrice() - discountAmount)
                        .setScale(2, RoundingMode.HALF_UP)
        );

        // New product → no reservations
        product.setReservedQuantity(0);

        // quantity - reservedQuantity
        product.setAvailableQuantity(
                Math.max(0, product.getQuantity() - product.getReservedQuantity())
        );

        product.setOutOfStock(
                product.getAvailableQuantity() == 0
        );

        // Every category ID must exist
        Set<Category> categories =
                helper.getCategoriesbyids(request.getCategories());

        product.setCategories(categories);
    }

    private void copyUpdateRequestToProduct(ProductUpdateRequestDto request, Product product) {
        product.setProductname(request.getProductname());
        product.setProductDescription(request.getProductDescription());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setReservedQuantity(0);
        product.setQuantity(request.getQuantity());
        // Recalculate available stock
        product.setAvailableQuantity(
                Math.max(0, product.getQuantity() - product.getReservedQuantity())
        );
        product.setProductStatus(request.getProductStatus());
        product.setLive(request.isLive());
        // calculated by the server, not sent by the client
        double discountAmount = request.getPrice() * request.getDiscountPercentage() / 100;
        product.setDiscountPrice(
                BigDecimal.valueOf(request.getPrice() - discountAmount).setScale(2, RoundingMode.HALF_UP)
        );
        product.setOutOfStock(product.getAvailableQuantity() == 0);
    }

    // Entity -> Response DTO (categories become a set of category ids)
    private ProductResponseDto entityToDto(Product product) {

        logger.debug("Converting product entity to response DTO");

        ProductResponseDto response = mapper.map(product, ProductResponseDto.class);
        response.setCategories(
                product.getCategories().stream()
                        .map(Category::getCategoryId)
                        .collect(Collectors.toSet())
        );

        return response;
    }
}