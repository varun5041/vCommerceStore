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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

        logger.info("Received request to create a new user");

        userDto user = userService.createUser(userdto);

        logger.info("User created successfully with id: {}", user.getUserId());

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    // Update
    @PutMapping("/update/{userId}")
    public ResponseEntity<userDto> updateUser(
            @Valid @RequestBody userDto userdto,
            @PathVariable String userId) {

        logger.info("Received request to update user with id: {}", userId);

        userDto updatedUser = userService.updateUser(userdto, userId);

        logger.info("User updated successfully with id: {}", userId);

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

        logger.info("Received request to get all users. Page: {}, Size: {}, SortBy: {}, Order: {}",
                pagenumber, pagesize, sortby, order);

        PageResopnse<userDto> users = userService.getAllUsers(pagenumber,pagesize,sortby,order);

        logger.info("Users fetched successfully for page: {}", pagenumber);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    // Delete
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId) throws IOException {

        logger.info("Received request to delete user with id: {}", userId);

        userService.deleteUser(userId);

        logger.info("User deleted successfully with id: {}", userId);

        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .message("User Deleted Successfully")
                .success(true)
                .httpStatus(HttpStatus.OK).build();

        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }


    // Get Single
    @GetMapping("/{userId}")
    public ResponseEntity<userDto> getUserById(@PathVariable String userId) {

        logger.info("Received request to get user with id: {}", userId);

        userDto user = userService.getUserById(userId);

        logger.info("User fetched successfully with id: {}", userId);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    // Get By Email
    @GetMapping("/email/{userEmail}")
    public ResponseEntity<userDto> getUserByEmail(@PathVariable String userEmail) {

        logger.info("Received request to get user by email: {}", userEmail);

        userDto user = userService.getUserByEmail(userEmail);

        logger.info("User fetched successfully using email: {}", userEmail);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    //search
    // Search
    @GetMapping("/search")
    public ResponseEntity<List<userDto>> searchUser(
            @RequestParam String keyword) {

        logger.info("Received request to search users with keyword: {}", keyword);

        List<userDto> users = userService.searchUser(keyword);

        logger.info("User search completed. Found {} users", users.size());

        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @PostMapping("/image/{userid}")
    public ResponseEntity<ImageResponse> uploadFile(
            @PathVariable String userid,
            @RequestParam("profileImage")MultipartFile profileimage
    ) throws IOException {

        logger.info("Received profile image upload request for user: {}", userid);

        logger.debug("Uploading profile image: {}", profileimage.getOriginalFilename());

        String imagename = fileService.uploadFile(profileimage,UserProfileImagePath);

        logger.info("Profile image uploaded successfully for user: {}", userid);

        String savedImagename = userService.saveUserProfileImage(userid,imagename);

        logger.info("Profile image reference saved successfully for user: {}", userid);

        ImageResponse response = ImageResponse.builder().imageName(profileimage.getOriginalFilename()).success(true).message("image saved successfully").httpStatus(HttpStatus.CREATED).build();

        logger.info("Profile image upload completed successfully for user: {}", userid);

        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }


    @GetMapping("/image/getProfileImage/{userid}")
    public ResponseEntity<Resource> getUserProfileImage(
            @PathVariable String userid
    ) throws FileNotFoundException {

        logger.info("Received request to get profile image for user: {}", userid);

        String name = userService.getProfileImagename(userid);

        logger.debug("Profile image name retrieved: {}", name);

        InputStream inputStream = null;

        if(name.equalsIgnoreCase("defaultProfile.jpg")){

            logger.debug("User {} is using default profile image", userid);

            inputStream = fileService.getResource(DefaultProfileImagePath, name);

        }else {

            logger.debug("User {} is using custom profile image: {}", userid, name);

            inputStream = fileService.getResource(UserProfileImagePath, name);
        }

        InputStreamResource resource =
                new InputStreamResource(inputStream);

        String extension = name.substring(name.lastIndexOf("."));

        logger.debug("Profile image extension: {}", extension);

        if (extension.equalsIgnoreCase(".png")) {

            logger.info("Returning PNG profile image for user: {}", userid);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        }

        if (extension.equalsIgnoreCase(".jpg")
                || extension.equalsIgnoreCase(".jpeg")) {

            logger.info("Returning JPEG profile image for user: {}", userid);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        }

        if (extension.equalsIgnoreCase(".gif")) {

            logger.info("Returning GIF profile image for user: {}", userid);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_GIF)
                    .body(resource);
        }

        logger.warn("Unsupported profile image type for user {}: {}", userid, extension);

        throw new InvalidFileTypeException("Unsupported image type!");
    }
}