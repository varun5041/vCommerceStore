package com.varun.vcommercestore.dtos;

import com.varun.vcommercestore.Enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class userDto {

    private String userId;

    private String userName;

    private String userEmail;

    private String userPassword;

    private Gender userGender;

    private String userAddress;

    private String userAbout;

    private String profileImage;

}
