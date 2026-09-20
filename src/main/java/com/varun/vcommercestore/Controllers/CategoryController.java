package com.varun.vcommercestore.Controllers;

import com.varun.vcommercestore.Exceptions.InvalidFileTypeException;
import com.varun.vcommercestore.Services.CategoryService;
import com.varun.vcommercestore.Services.FileService;
import com.varun.vcommercestore.dtos.Requestdtos.categoryDto;
import com.varun.vcommercestore.dtos.Responcedtos.CategoryResponseDto;
import com.varun.vcommercestore.dtos.Responcedtos.ProductResponseDto;
import com.varun.vcommercestore.dtos.ResponseEntities.ImageResponse;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
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
@RequestMapping("/category")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private CategoryService categoryService;

    @Value("${category.icons.path}")
    private String CategoryImagePath;

    @Value("${default.cateory.icons.path}")
    String DefaultCategoryiconPath;

    //create
    @PostMapping("/create")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody categoryDto categoryRequest){

        logger.info("Received request to create category");
        CategoryResponseDto savedCategory = categoryService.createCategory(categoryRequest);

        logger.info("Category created successfully with id: {}", savedCategory.getCategoryId());

        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }


    //update
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @Valid @RequestBody categoryDto categoryRequest,
            @PathVariable String categoryId){

        logger.info("Received request to update category with id: {}", categoryId);

        CategoryResponseDto updatedCategory = categoryService.updateCategory(categoryRequest, categoryId);

        logger.info("Category updated successfully with id: {}", categoryId);

        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }


    //delete
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable String categoryId) throws IOException {

        logger.info("Received request to delete category with id: {}", categoryId);

        categoryService.deleteCategory(categoryId);

        logger.info("Category deleted successfully with id: {}", categoryId);

        return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
    }


    //getall
    @GetMapping("/getall")
    public ResponseEntity<PageResopnse<CategoryResponseDto>> getAllCategories(
            @RequestParam(value = "pagenumber",defaultValue = "0",required = false) int pagenumber,
            @RequestParam(value = "pagesize",defaultValue = "10",required = false)int pagesize,
            @RequestParam(value = "sortby",defaultValue = "title",required = false) String sortby,
            @RequestParam(value = "order",defaultValue ="asc",required = false) String order
    ){

        logger.info("Received request to get all categories. Page: {}, Size: {}, SortBy: {}, Order: {}",
                pagenumber, pagesize, sortby, order);

        PageResopnse<CategoryResponseDto> categories =
                categoryService.getAllCategories(pagenumber,pagesize,sortby,order);

        logger.info("Categories fetched successfully");

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }


    //getsinglebyid
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable String categoryId){

        logger.info("Received request to get category with id: {}", categoryId);

        CategoryResponseDto category = categoryService.getCategoryById(categoryId);

        logger.info("Category fetched successfully with id: {}", categoryId);

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    //get all products by category
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<List<ProductResponseDto>> getProductsFromCategory(
            @PathVariable String categoryId) {

        List<ProductResponseDto> products = categoryService.getProductFromCategory(categoryId);

        return ResponseEntity.ok(products);
    }

    @PostMapping("/upload/image/{categoryid}")
    public ResponseEntity<ImageResponse> saveCategoryImage(
            @PathVariable String categoryid,
            @RequestParam(name="categoryimage") MultipartFile categoryimage
    ) throws IOException {

        logger.info("Received category image upload request for category: {}", categoryid);

        logger.debug("Category image name: {}", categoryimage.getOriginalFilename());

        String name = fileService.uploadFile(categoryimage,CategoryImagePath);

        logger.info("Category image uploaded successfully for category: {}", categoryid);

        String savedImageName = categoryService.savecategoryImageName(name,categoryid);

        logger.info("Category image name saved successfully for category: {}", categoryid);

        ImageResponse response= ImageResponse.builder()
                .imageName(categoryimage.getOriginalFilename())
                .message("Category Image Saved Sucessfully")
                .success(true).httpStatus(HttpStatus.CREATED)
                .build();

        logger.info("Category image upload completed successfully for category: {}", categoryid);

        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/image/{categoryid}")
    public ResponseEntity<Resource> getCategoryImage(
            @PathVariable String categoryid
    ) throws FileNotFoundException {

        logger.info("Received request to get category image for category: {}", categoryid);

        String name = categoryService.getCategoryImageName(categoryid);

        logger.debug("Category image name retrieved: {}", name);
        InputStream inputStream=null;
        if(!name.equalsIgnoreCase("defcaticon.png")) {
            inputStream = fileService.getResource(CategoryImagePath, name);
        }else {
            inputStream = fileService.getResource(DefaultCategoryiconPath,name);
        }
        logger.debug("Category image resource loaded successfully for category: {}", categoryid);

        InputStreamResource resource = new InputStreamResource(inputStream);

        String extension = name.substring(name.lastIndexOf("."));

        logger.debug("category image extension: {}", extension);

        if (extension.equalsIgnoreCase(".png")) {

            logger.info("Returning PNG category image for category: {}", categoryid);

            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
        }

        if (extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg")) {

            logger.info("Returning JPEG category image for category: {}", categoryid);

            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
        }

        if (extension.equalsIgnoreCase(".gif")) {

            logger.info("Returning GIF category image for category: {}", categoryid);

            return ResponseEntity.ok().contentType(MediaType.IMAGE_GIF).body(resource);
        }

        logger.warn("Unsupported category image type for category {}: {}", categoryid, extension);

        throw new InvalidFileTypeException("Unsupported image type!");
    }
}