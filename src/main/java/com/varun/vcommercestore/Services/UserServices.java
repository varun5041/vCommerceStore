package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.dtos.Requestdtos.User.UserRequestDto;
import com.varun.vcommercestore.dtos.Responcedtos.UserResponseDto;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import com.varun.vcommercestore.dtos.UpdateRequestDto.UserUpdateRequestDto;

import java.io.IOException;
import java.util.List;

public interface UserServices {
    // Create
    UserResponseDto createUser(UserRequestDto userRequestDtodto);

    // Update
    UserResponseDto updateUser(UserUpdateRequestDto usereqdto,String userid);

    // Delete
    void deleteUser(String userId) throws IOException;

    // Get user by ID
    UserResponseDto getUserById(String userId);

    //getByEmail
    UserResponseDto getUserByEmail(String userEmail);

    //searchUser
    List<UserResponseDto> searchUser(String keyword);

    // Get all users
    PageResopnse<UserResponseDto> getAllUsers(int pagenumber, int pagesize, String sortby, String order);

    String saveUserProfileImage(String userid,String userImage);

    String getProfileImagename(String userid);
}
