package com.varun.vcommercestore.dtos.Responcedtos;

import com.varun.vcommercestore.Models.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {

    private String CategoryId;
    private String title;
    private String CategoryIcon;
    private String CategoryDescription;
}
