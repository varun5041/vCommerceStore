package com.varun.vcommercestore.Services.UserServiceImpl;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.Category;
import com.varun.vcommercestore.Models.Product;
import com.varun.vcommercestore.Repositories.CategoryRepository;
import com.varun.vcommercestore.Services.CategoryService;
import com.varun.vcommercestore.Services.FileService;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService{

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


    @Override
    public categoryDto createCategory(categoryDto categoryDto) {

        logger.info("Creating new category");
        categoryDto.setCategoryId(UUID.randomUUID().toString());
        if(categoryDto.getCategoryIcon()==null) {
            categoryDto.setCategoryIcon("defcaticon.png");
        }
        Category category = dtotoEntity(categoryDto);
        logger.debug("Category DTO converted to entity");
        Category savedCategory = categoryRepository.save(category);
        logger.info("Category created successfully with id: {}", savedCategory.getCategoryId());
        categoryDto savedcategoryDto = entityToDto(savedCategory);
        return savedcategoryDto;
    }

    @Override
    public categoryDto updateCategory(categoryDto categoryDto, String categoryId) {

        logger.info("Updating category with id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CATEGORY NOT FOUND!"));

        logger.debug("Category found with id: {}", categoryId);

        category.setTitle(categoryDto.getTitle());
        category.setCategoryIcon(categoryDto.getCategoryIcon());
        category.setCategoryDescription(categoryDto.getCategoryDescription());

        Category updatedCategory = categoryRepository.save(category);

        logger.info("Category updated successfully with id: {}", categoryId);

        return entityToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(String categoryId) throws IOException {

        logger.info("Deleting category with id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CATEGORY NOT FOUND!"));

        String categoryicon = category.getCategoryIcon();
        if(categoryicon!=null && !categoryicon.equalsIgnoreCase("defcaticon.png")){
            fileService.deleteFile(categoryicon,CategoryImagePath);
        }
        logger.debug("Category found with id: {}", categoryId);

        categoryRepository.delete(category);

        logger.info("Category deleted successfully with id: {}", categoryId);
    }

    @Override
    public PageResopnse<categoryDto> getAllCategories(int pagenumber, int pagesize, String sortby, String order) {

        logger.info("Fetching all categories. Page: {}, Size: {}, SortBy: {}, Order: {}",
                pagenumber, pagesize, sortby, order);

        Sort sorted = order.equalsIgnoreCase("desc") ?
                Sort.by(sortby).descending() :
                Sort.by(sortby).ascending();

        Pageable pageable = PageRequest.of(pagenumber, pagesize, sorted);

        Page<Category> page = categoryRepository.findAll(pageable);

        logger.info("Categories fetched successfully. Total categories: {}",
                page.getTotalElements());

        return helper.getPageResponse(page, categoryDto.class);
    }

    @Override
    public categoryDto getCategoryById(String categoryId) {

        logger.info("Fetching category with id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CATEGORY NOT FOUND!"));

        logger.debug("Category found with id: {}", categoryId);

        return entityToDto(category);
    }

    @Override
    public String savecategoryImageName(String name,String categoryid) {
        Category category = categoryRepository.findById(categoryid)
                .orElseThrow(()->new ResourceNotFoundException("Category not Found!"));
        category.setCategoryIcon(name);
        categoryRepository.save(category);
        return name;
    }

    @Override
    public String getCategoryImageName(String categoryid) {
        Category category = categoryRepository.findById(categoryid).orElseThrow(()->new ResourceNotFoundException("Category Does Not Exist"));
        String name = category.getCategoryIcon();
        return name;
    }

    public List<ProductDto> getProductFromCategory(String CategoryId){
        Category category = categoryRepository.findById(CategoryId).orElseThrow(()->new ResourceNotFoundException("Category Not Found"));
        Set<Product> allProducts = category.getProducts();
        List<ProductDto> productDtoList = allProducts.stream()
                .map(product->entityToDtoforProducts(product))
                .collect(Collectors.toList());

        return productDtoList;
    }






    //-----------------------------------------------------
    //mapper methods
    //----------------------------------------------------
    public Category dtotoEntity(categoryDto dto){

        logger.debug("Converting category DTO to entity");

        return mapper.map(dto,Category.class);
    }

    public categoryDto entityToDto(Category category){

        logger.debug("Converting category entity to DTO");

        return mapper.map(category, categoryDto.class);
    }

    public ProductDto entityToDtoforProducts(Product product){

        logger.debug("Converting product entity to DTO");

        return mapper.map(product,ProductDto.class);
    }


}