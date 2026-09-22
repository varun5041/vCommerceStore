package com.varun.vcommercestore.Controllers;

import com.varun.vcommercestore.Exceptions.InvalidFileTypeException;
import com.varun.vcommercestore.Services.FileService;
import com.varun.vcommercestore.Services.ProductServies;
import com.varun.vcommercestore.dtos.Requestdtos.User.ProductRequestDto;
import com.varun.vcommercestore.dtos.Responcedtos.ProductResponseDto;
import com.varun.vcommercestore.dtos.ResponseEntities.ImageResponse;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import com.varun.vcommercestore.dtos.UpdateRequestDto.ProductUpdateRequestDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String ProductImagePath;

    @Value("${product.default.image.path}")
    private String DefaultProductImagePath;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductServies productServies;


    //create
    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto){
        logger.info("Received request to create product");
        ProductResponseDto savedProduct = productServies.createProduct(productRequestDto);
        logger.info("Product created successfully with id: {}",savedProduct.getProductid());
        return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
    }


    //update
    @PutMapping("/update/{ProductId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@Valid @RequestBody ProductUpdateRequestDto productRequestDto,
                                                            @PathVariable String ProductId){
        logger.info("Received request to update product with id: {}",ProductId);
        ProductResponseDto updatedProduct = productServies.updateProdcut(productRequestDto,ProductId);
        logger.info("Product updated successfully with id: {}",ProductId);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }


    //delete
    @DeleteMapping("/delete/{ProductId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String ProductId) throws IOException {
        logger.info("Received request to delete product with id: {}",ProductId);
        productServies.deleteProduct(ProductId);
        logger.info("Product deleted successfully with id: {}",ProductId);
        return new ResponseEntity<>("Product deleted successfully",HttpStatus.OK);
    }


    //get single product
    @GetMapping("/{ProductId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String ProductId){
        logger.info("Received request to get product with id: {}",ProductId);
        ProductResponseDto product = productServies.getByid(ProductId);
        logger.info("Product fetched successfully with id: {}",ProductId);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }


    //get all products
    @GetMapping("/getall")
    public ResponseEntity<PageResopnse<ProductResponseDto>> getAllProducts(
            @RequestParam(value = "pagenumber",defaultValue = "0",required = false) int pagenumber,
            @RequestParam(value = "pagesize",defaultValue = "10",required = false) int pagesize,
            @RequestParam(value = "sortby",defaultValue = "productname",required = false) String sortby,
            @RequestParam(value = "order",defaultValue = "asc",required = false) String order
    ){

        logger.info("Received request to get all products. Page: {}, Size: {}, SortBy: {}, Order: {}",
                pagenumber,pagesize,sortby,order);
        PageResopnse<ProductResponseDto> products =
                productServies.getAllProducts(pagenumber,pagesize,sortby,order);
        logger.info("Products fetched successfully");
        return new ResponseEntity<>(products,HttpStatus.OK);
    }

    //search
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProductsGlobal(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "minprice", required = false) Double minprice,
            @RequestParam(value = "maxprice", required = false) Double maxprice,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "categoryId", required = false) String categoryId
    ) {
        List<ProductResponseDto> results = productServies.searchProducts(keyword, brand, minprice, maxprice, categoryId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }


    //get all live products
    @GetMapping("/live")
    public ResponseEntity<List<ProductResponseDto>> getAllLiveProducts(){
        logger.info("Received request to get all live products");
        List<ProductResponseDto> liveProducts = productServies.getallLiveProducts();
        logger.info("Live products fetched successfully");
        return new ResponseEntity<>(liveProducts,HttpStatus.OK);
    }


    //search product by name
    @GetMapping("/search/name")
    public ResponseEntity<List<ProductResponseDto>> searchProductByName(@RequestParam String keyword){
        logger.info("Received request to search product by name: {}",keyword);
        List<ProductResponseDto> products = productServies.searchProductByname(keyword);
        logger.info("Product name search completed successfully");
        return new ResponseEntity<>(products,HttpStatus.OK);
    }


    //search product by brand
    @GetMapping("/search/brand")
    public ResponseEntity<List<ProductResponseDto>> searchByBrand(@RequestParam String brand){
        logger.info("Received request to search product by brand: {}",brand);
        List<ProductResponseDto> products = productServies.searchByBrand(brand);
        logger.info("Brand search completed successfully");
        return new ResponseEntity<>(products,HttpStatus.OK);
    }

    @PostMapping("/upload/image/{ProductId}")
    public ResponseEntity<ImageResponse> saveProductImage(
            @PathVariable String ProductId,
            @RequestParam(name="productimage") MultipartFile productimage
    ) throws IOException {
        logger.info("Received product image upload request for product: {}", ProductId);
        logger.debug("Product image name: {}", productimage.getOriginalFilename());
        String name = fileService.uploadFile(productimage,ProductImagePath);
        logger.info("Product image uploaded successfully for product: {}", ProductId);
        String savedImageName = productServies.saveProductImageName(name,ProductId);
        logger.info("Product image name saved successfully for product: {}", ProductId);
        ImageResponse response= ImageResponse.builder()
                .imageName(productimage.getOriginalFilename())
                .message("Product Image Saved Sucessfully")
                .success(true).httpStatus(HttpStatus.CREATED)
                .build();
        logger.info("Product image upload completed successfully for product: {}", ProductId);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/image/{ProductId}")
    public ResponseEntity<Resource> getProductImage(
            @PathVariable String ProductId
    ) throws FileNotFoundException {
        logger.info("Received request to get product image for product: {}", ProductId);
        String name = productServies.getProductImageName(ProductId);
        logger.debug("Product image name retrieved: {}", name);
        InputStream inputStream=null;
        if(!name.equalsIgnoreCase("defproductimage.jpg")) {
            inputStream = fileService.getResource(ProductImagePath,name);
        }else {
            inputStream = fileService.getResource(DefaultProductImagePath,name);
        }
        logger.debug("Product image resource loaded successfully for product: {}", ProductId);
        InputStreamResource resource = new InputStreamResource(inputStream);
        String extension = name.substring(name.lastIndexOf("."));
        logger.debug("Product image extension: {}", extension);
        if (extension.equalsIgnoreCase(".png")) {
            logger.info("Returning PNG product image for product: {}", ProductId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
        }
        if (extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg")) {
            logger.info("Returning JPEG product image for product: {}", ProductId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
        }
        if (extension.equalsIgnoreCase(".gif")) {
            logger.info("Returning GIF product image for product: {}", ProductId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_GIF).body(resource);
        }
        logger.warn("Unsupported product image type for product {}: {}", ProductId, extension);
        throw new InvalidFileTypeException("Unsupported image type!");
    }
}