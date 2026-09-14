package com.varun.vcommercestore.Controllers;

import com.varun.vcommercestore.Models.User;
import com.varun.vcommercestore.Services.UserServices;
import com.varun.vcommercestore.dtos.ApiResponseMessage;
import com.varun.vcommercestore.dtos.userDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServices userService;

    //create
    @PostMapping("/create")
    public ResponseEntity<userDto> createUser(@Valid @RequestBody userDto userdto){
        userDto user = userService.createUser(userdto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // Update
    @PutMapping("/update/{userId}")
    public ResponseEntity<userDto> updateUser(
            @Valid @RequestBody userDto userdto,
            @PathVariable String userId) {

        userDto updatedUser = userService.updateUser(userdto, userId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }


    // Get All
    @GetMapping("/getall")
    public ResponseEntity<List<userDto>> getAllUsers(
            @RequestParam(value = "pagenumber",defaultValue = "0",required = false) int pagenumber,
            @RequestParam(value = "pagesize",defaultValue = "10",required = false)int pagesize
    ) {

        List<userDto> users = userService.getAllUsers(pagenumber,pagesize);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    // Delete
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .message("User Deleted Successfully")
                .success(true)
                .httpStatus(HttpStatus.OK).build();

        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }


    // Get Single
    @GetMapping("/{userId}")
    public ResponseEntity<userDto> getUserById(@PathVariable String userId) {

        userDto user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    // Get By Email
    @GetMapping("/email/{userEmail}")
    public ResponseEntity<userDto> getUserByEmail(@PathVariable String userEmail) {

        userDto user = userService.getUserByEmail(userEmail);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //search
    // Search
    @GetMapping("/search")
    public ResponseEntity<List<userDto>> searchUser(
            @RequestParam String keyword) {

        List<userDto> users = userService.searchUser(keyword);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }


}
