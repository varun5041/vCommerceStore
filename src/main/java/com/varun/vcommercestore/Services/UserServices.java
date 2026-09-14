package com.varun.vcommercestore.Services;

import com.varun.vcommercestore.Models.User;
import com.varun.vcommercestore.dtos.userDto;

import java.util.List;

public interface UserServices {
    // Create
    userDto createUser(userDto userdto);

    // Update
    userDto updateUser(userDto userdto, String userId);

    // Delete
    void deleteUser(String userId);

    // Get user by ID
    userDto getUserById(String userId);

    //getByEmail
    userDto getUserByEmail(String userEmail);

    //searchUser
    List<userDto> searchUser(String keyword);

    // Get all users
    List<userDto> getAllUsers(int pagenumber,int pagesize);
}
