package com.varun.vcommercestore.dtos;

import com.varun.vcommercestore.Models.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class categoryDto {

    private String CategoryId;
    @NotBlank(message = "Category name is Required!")
    @Size(min = 3 ,max = 50,message = "size excedded for title please enter shorter title")
    private String title;
    private String CategoryIcon;
    @NotBlank(message = "Please Give a Small description")
    private String CategoryDescription;
    private Set<Product> products = new HashSet<>();
}
