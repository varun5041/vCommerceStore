package com.varun.vcommercestore.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
}
