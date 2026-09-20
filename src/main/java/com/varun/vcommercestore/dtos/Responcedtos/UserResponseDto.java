package com.varun.vcommercestore.dtos.Responcedtos;

import com.varun.vcommercestore.Enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private String userId;
    private String userName;
    private String userEmail;
    private Gender userGender;
    private String userAddress;
    private String userAbout;
    private String profileImage;
}
