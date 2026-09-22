package com.varun.vcommercestore.Models;

import com.varun.vcommercestore.Enums.Gender;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User {
    @Id
    private String userId;
    @Column(unique = true,nullable = false)
    private String userName;
    @Column(nullable = false, unique = true, length = 100)
    private String userEmail;
    private String userPassword;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender userGender;
    private String userAddress;
    private String userAbout;
    private String profileImage;
    //make a new cart when a new user is created
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "cart_id",
            unique = true
    )
    private Cart cart;
}
