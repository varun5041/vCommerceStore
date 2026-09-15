package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.Models.User;
import com.varun.vcommercestore.dtos.PageResopnse;
import com.varun.vcommercestore.dtos.userDto;

import java.io.IOException;
import java.util.List;

public interface UserServices {
    // Create
    userDto createUser(userDto userdto);

    // Update
    userDto updateUser(userDto userdto, String userId);

    // Delete
    void deleteUser(String userId) throws IOException;

    // Get user by ID
    userDto getUserById(String userId);

    //getByEmail
    userDto getUserByEmail(String userEmail);

    //searchUser
    List<userDto> searchUser(String keyword);

    // Get all users
    PageResopnse<userDto> getAllUsers(int pagenumber, int pagesize, String sortby, String order);

    String saveUserProfileImage(String userid,String userImage);

    String getProfileImagename(String userid);
}
