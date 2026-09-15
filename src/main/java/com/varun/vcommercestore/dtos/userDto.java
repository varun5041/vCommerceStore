package com.varun.vcommercestore.dtos;

import com.varun.vcommercestore.Enums.Gender;
import com.varun.vcommercestore.Validation.ImageNameValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class userDto {

    private String userId;

    @NotBlank(message = "Name is required")
    @Pattern(
            regexp = "^[A-Za-z ]{2,30}$",
            message = "Name must contain only letters and spaces"
    )
    @Size(
            min = 2,
            max = 30,
            message = "Name should be 2-30 characters"
    )
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid User Email")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid User Email"
    )
    private String userEmail;

    @NotBlank(message = "Password is Required!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,50}$",
            message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String userPassword;

    @NotNull(message = "Gender is required")
    private Gender userGender;

    @NotBlank(message = "Please Mention Address")
    @Size(
            max = 255,
            message = "Address cannot exceed 255 characters"
    )
    private String userAddress;

    @NotBlank(message = "Please Mention About")
    @Size(
            max = 500,
            message = "About cannot exceed 500 characters"
    )
    private String userAbout;

    @Pattern(
            regexp = "^(https?://).+",
            message = "Invalid profile image URL"
    )


    private String profileImage;
}