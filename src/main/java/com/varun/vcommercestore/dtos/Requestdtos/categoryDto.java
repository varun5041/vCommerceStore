package com.varun.vcommercestore.dtos.Requestdtos;

import com.varun.vcommercestore.Models.Product;
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

    @NotBlank(message = "Category name is Required!")
    @Size(min = 3 ,max = 50,message = "size excedded for title please enter shorter title")
    private String title;

    @NotBlank(message = "Please Give a Small description")
    private String CategoryDescription;

}
