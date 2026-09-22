package com.varun.vcommercestore.Utils;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.Category;
import com.varun.vcommercestore.Repositories.CategoryRepository;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Helper {
    @Autowired
    private ModelMapper mapper;
    @Autowired
    CategoryRepository categoryRepository;

    //U is the entity
    //V is the entity dto
    public <U,V> PageResopnse<V> getPageResponse(Page<U> page,Class<V> type){
        //getting all entity from page
        List<U> entity = page.getContent();

        //converting entity to dtos
        List<V> dtolist = entity.stream().map(obj-> mapper.map(obj,type)).collect(Collectors.toList());

        //making pageResponse
        PageResopnse<V> DtoPageResopnse = PageResopnse.<V>builder()
                .content(dtolist)
                .ppagenumber(page.getNumber())
                .pagesize(page.getSize())
                .totalElements(page.getTotalElements())
                .lastpage(page.isLast())
                .totalpages(page.getTotalPages())
                .build();

        return DtoPageResopnse;
    }

    public Set<Category> getCategoriesbyids(Set<String> categoryids){
        List<Category> categories = categoryRepository.findAllById(categoryids);
        if (categories.size() != categoryids.size()) {
            throw new ResourceNotFoundException(
                    "One or more category IDs are invalid"
            );
        }
        return new HashSet<>(categories);
    }
}
