package com.varun.vcommercestore.Controllers;

import com.varun.vcommercestore.Exceptions.InvalidFileTypeException;
import com.varun.vcommercestore.Models.User;
import com.varun.vcommercestore.Services.FileService;
import com.varun.vcommercestore.Services.UserServices;
import com.varun.vcommercestore.dtos.ApiResponseMessage;
import com.varun.vcommercestore.dtos.ImageResponse;
import com.varun.vcommercestore.dtos.PageResopnse;
import com.varun.vcommercestore.dtos.userDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServices userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String UserProfileImagePath;

    @Value("${user.profile.delfaultimage.path}")
    private String DefaultProfileImagePath;
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
    public ResponseEntity<PageResopnse<userDto>> getAllUsers(
            @RequestParam(value = "pagenumber",defaultValue = "0",required = false) int pagenumber,
            @RequestParam(value = "pagesize",defaultValue = "10",required = false)int pagesize,
            @RequestParam(value = "sortby",defaultValue = "userName",required = false) String sortby,
            @RequestParam(value = "order",defaultValue ="asc",required = false) String order
    ){

        PageResopnse<userDto> users = userService.getAllUsers(pagenumber,pagesize,sortby,order);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    // Delete
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId) throws IOException {
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

    @PostMapping("/image/{userid}")
    public ResponseEntity<ImageResponse> uploadFile(
            @PathVariable String userid,
            @RequestParam("profileImage")MultipartFile profileimage
    ) throws IOException {
        String imagename = fileService.uploadFile(profileimage,UserProfileImagePath);
        String savedImagename = userService.saveUserProfileImage(userid,imagename);
        ImageResponse response = ImageResponse.builder().imageName(profileimage.getOriginalFilename()).success(true).message("image saved successfully").httpStatus(HttpStatus.CREATED).build();
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/image/getProfileImage/{userid}")
    public ResponseEntity<Resource> getUserProfileImage(
            @PathVariable String userid
    ) throws FileNotFoundException {

        String name = userService.getProfileImagename(userid);
        InputStream inputStream = null;

        if(name.equalsIgnoreCase("defaultProfile.jpg")){
            inputStream = fileService.getResource(DefaultProfileImagePath, name);
        }else {
            inputStream = fileService.getResource(UserProfileImagePath, name);
        }
        InputStreamResource resource =
                new InputStreamResource(inputStream);

        String extension = name.substring(name.lastIndexOf("."));

        if (extension.equalsIgnoreCase(".png")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        }

        if (extension.equalsIgnoreCase(".jpg")
                || extension.equalsIgnoreCase(".jpeg")) {

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        }

        if (extension.equalsIgnoreCase(".gif")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_GIF)
                    .body(resource);
        }

        throw new InvalidFileTypeException("Unsupported image type!");
    }
}
