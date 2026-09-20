package com.varun.vcommercestore.Services.UserServiceImpl;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.Category;
import com.varun.vcommercestore.Models.Product;
import com.varun.vcommercestore.Repositories.CategoryRepository;
import com.varun.vcommercestore.Services.CategoryService;
import com.varun.vcommercestore.Services.FileService;
import com.varun.vcommercestore.Utils.Helper;
import com.varun.vcommercestore.dtos.Requestdtos.categoryDto;
import com.varun.vcommercestore.dtos.Responcedtos.CategoryResponseDto;
import com.varun.vcommercestore.dtos.Responcedtos.ProductResponseDto;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Helper helper;

    @Autowired
    private FileService fileService;

    @Value("${category.icons.path}")
    private String CategoryImagePath;


    // =========================
    // CREATE CATEGORY
    // =========================
    @Override
    public CategoryResponseDto createCategory(categoryDto categoryRequest) {

        logger.info("Creating new category");

        Category category = dtoToEntity(categoryRequest);

        // fields the server sets (client can't send these)
        category.setCategoryId(UUID.randomUUID().toString());
        category.setCategoryIcon("defcaticon.png");

        Category savedCategory = categoryRepository.save(category);

        logger.info("Category created successfully with id: {}", savedCategory.getCategoryId());

        return entityToDto(savedCategory);
    }


    // =========================
    // UPDATE CATEGORY
    // =========================
    @Override
    public CategoryResponseDto updateCategory(categoryDto categoryRequest, String categoryId) {

        logger.info("Updating category with id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CATEGORY NOT FOUND!"));

        logger.debug("Category found with id: {}", categoryId);

        // only title and description can be updated here
        // (the icon changes only through the image upload endpoint)
        category.setTitle(categoryRequest.getTitle());
        category.setCategoryDescription(categoryRequest.getCategoryDescription());

        Category updatedCategory = categoryRepository.save(category);

        logger.info("Category updated successfully with id: {}", categoryId);

        return entityToDto(updatedCategory);
    }


    // =========================
    // DELETE CATEGORY
    // =========================
    @Override
    public void deleteCategory(String categoryId) throws IOException {

        logger.info("Deleting category with id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CATEGORY NOT FOUND!"));

        logger.debug("Category found with id: {}", categoryId);

        String categoryicon = category.getCategoryIcon();

        // delete the DB row first, then the icon file
        categoryRepository.delete(category);

        if (categoryicon != null && !categoryicon.equalsIgnoreCase("defcaticon.png")) {
            fileService.deleteFile(categoryicon, CategoryImagePath);
        }

        logger.info("Category deleted successfully with id: {}", categoryId);
    }


    // =========================
    // GET ALL CATEGORIES (PAGED)
    // =========================
    @Override
    public PageResopnse<CategoryResponseDto> getAllCategories(int pagenumber, int pagesize, String sortby, String order) {

        logger.info("Fetching all categories. Page: {}, Size: {}, SortBy: {}, Order: {}",
                pagenumber, pagesize, sortby, order);

        Sort sorted = order.equalsIgnoreCase("desc") ?
                Sort.by(sortby).descending() :
                Sort.by(sortby).ascending();

        Pageable pageable = PageRequest.of(pagenumber, pagesize, sorted);

        Page<Category> page = categoryRepository.findAll(pageable);

        logger.info("Categories fetched successfully. Total categories: {}",
                page.getTotalElements());

        return helper.getPageResponse(page, CategoryResponseDto.class);
    }


    // =========================
    // GET CATEGORY BY ID
    // =========================
    @Override
    public CategoryResponseDto getCategoryById(String categoryId) {

        logger.info("Fetching category with id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CATEGORY NOT FOUND!"));

        logger.debug("Category found with id: {}", categoryId);

        return entityToDto(category);
    }


    // =========================
    // IMAGE RELATED
    // =========================
    @Override
    public String savecategoryImageName(String name, String categoryid) {
        Category category = categoryRepository.findById(categoryid)
                .orElseThrow(() -> new ResourceNotFoundException("Category not Found!"));
        category.setCategoryIcon(name);
        categoryRepository.save(category);
        return name;
    }

    @Override
    public String getCategoryImageName(String categoryid) {
        Category category = categoryRepository.findById(categoryid)
                .orElseThrow(() -> new ResourceNotFoundException("Category Does Not Exist"));
        return category.getCategoryIcon();
    }


    // =========================
    // PRODUCTS OF A CATEGORY
    // =========================
    @Override
    public List<ProductResponseDto> getProductFromCategory(String CategoryId) {
        Category category = categoryRepository.findById(CategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category Not Found"));

        Set<Product> allProducts = category.getProducts();

        return allProducts.stream()
                .map(this::productToResponse)
                .collect(Collectors.toList());
    }


    //-----------------------------------------------------
    //mapper methods
    //----------------------------------------------------

    // Request DTO -> Entity (only the two fields the client sends)
    private Category dtoToEntity(categoryDto request) {

        logger.debug("Converting category request DTO to entity");

        Category category = new Category();
        category.setTitle(request.getTitle());
        category.setCategoryDescription(request.getCategoryDescription());
        return category;
    }

    // Entity -> Response DTO
    private CategoryResponseDto entityToDto(Category category) {

        logger.debug("Converting category entity to response DTO");

        return mapper.map(category, CategoryResponseDto.class);
    }

    // Product entity -> ProductResponseDto (categories become a set of ids)
    private ProductResponseDto productToResponse(Product product) {

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