package com.varun.vcommercestore.Repositories;

import com.varun.vcommercestore.Models.Category;
import com.varun.vcommercestore.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {
    List<Category> findAllById(Iterable<String> ids);

}
