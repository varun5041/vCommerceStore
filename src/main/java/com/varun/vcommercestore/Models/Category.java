package com.varun.vcommercestore.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name= "categories")
public class Category {
    @Id
    @Column(name = "id")
    private String CategoryId;
    @Column(name = "categoryname",length = 25 ,nullable = false)
    private String title;
    private String CategoryIcon;
    @Column(name="description",length=225)
    private String CategoryDescription;
    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();
}
