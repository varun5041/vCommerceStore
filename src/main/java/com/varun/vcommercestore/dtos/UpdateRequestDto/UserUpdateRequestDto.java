package com.varun.vcommercestore.dtos.UpdateRequestDto;

import com.varun.vcommercestore.Enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequestDto {
    @NotBlank private String userName;
    @NotNull
    private Gender userGender;
    @NotBlank private String userAddress;
    @NotBlank
    private String userAbout;
}
