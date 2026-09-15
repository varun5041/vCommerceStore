package com.varun.vcommercestore.Services.UserServiceImpl;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.User;
import com.varun.vcommercestore.Repositories.UserRepository;
import com.varun.vcommercestore.Services.FileService;
import com.varun.vcommercestore.Services.UserServices;
import com.varun.vcommercestore.Utils.Helper;
import com.varun.vcommercestore.dtos.ResponseEntities.PageResopnse;
import com.varun.vcommercestore.dtos.userDto;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserServices {

    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private Helper helper;

    @Value("${user.profile.image.path}")
    private String userProfileImagePath;


    // =========================
    // CREATE USER
    // =========================

    @Override
    public userDto createUser(userDto userdto) {

        logger.info("Creating new user with email: {}",
                userdto.getUserEmail());

        if (userRepository.existsByUserEmail(userdto.getUserEmail())) {

            logger.warn("User creation failed. Email already exists: {}",
                    userdto.getUserEmail());

            throw new DataIntegrityViolationException(
                    "Account with this email already exists!"
            );
        }

        if (userRepository.existsByUserName(userdto.getUserName())) {

            logger.warn("User creation failed. Username already exists: {}",
                    userdto.getUserName());

            throw new DataIntegrityViolationException(
                    "Account with Username already exists!"
            );
        }

        String userId = UUID.randomUUID().toString();
        userdto.setUserId(userId);

        logger.debug("Generated user id: {}", userId);

        if (userdto.getProfileImage() == null) {

            userdto.setProfileImage("defaultProfile.jpg");

            logger.debug("No profile image provided. Using default profile image.");
        }

        User user = dtoToEntity(userdto);

        User savedUser = userRepository.save(user);

        logger.info("User created successfully with id: {}",
                savedUser.getUserId());

        return entityToDto(savedUser);
    }


    // =========================
    // UPDATE USER
    // =========================

    @Override
    public userDto updateUser(userDto userdto, String userId) {

        logger.info("Updating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Update failed. User not found with id: {}",
                            userId);

                    return new ResourceNotFoundException(
                            "USER NOT FOUND!"
                    );
                });

        user.setUserName(userdto.getUserName());
        user.setUserEmail(userdto.getUserEmail());
        user.setUserPassword(userdto.getUserPassword());
        user.setUserGender(userdto.getUserGender());
        user.setUserAddress(userdto.getUserAddress());
        user.setUserAbout(userdto.getUserAbout());
        user.setProfileImage(userdto.getProfileImage());

        User updatedUser = userRepository.save(user);

        logger.info("User updated successfully with id: {}",
                updatedUser.getUserId());

        return entityToDto(updatedUser);
    }


    // =========================
    // DELETE USER
    // =========================

    @Override
    public void deleteUser(String userId) throws IOException {

        logger.info("Starting deletion of user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Delete failed. User not found with id: {}",
                            userId);

                    return new ResourceNotFoundException(
                            "USER NOT FOUND!"
                    );
                });

        logger.debug("User found with id: {}", userId);

        String imageName = user.getProfileImage();

        if (imageName != null && !imageName.equalsIgnoreCase("defaultProfile.jpg")) {
            logger.debug("Deleting profile image '{}' for user '{}'", imageName, userId);
            fileService.deleteFile(imageName, userProfileImagePath);
            logger.info("Profile image '{}' deleted successfully", imageName);
        } else {
            logger.debug("User has default/no profile image. Skipping image deletion.");
        }

        userRepository.delete(user);

        logger.info("User deleted successfully with id: {}", userId);
    }


    // =========================
    // GET USER BY ID
    // =========================

    @Override
    public userDto getUserById(String userId) {

        logger.info("Fetching user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", userId);

                    return new ResourceNotFoundException(
                            "USER NOT FOUND!"
                    );
                });

        logger.debug("User found with id: {}", userId);

        return entityToDto(user);
    }


    // =========================
    // GET USER BY EMAIL
    // =========================

    @Override
    public userDto getUserByEmail(String userEmail) {

        logger.info("Fetching user with email: {}", userEmail);

        Optional<User> userOptional =
                userRepository.findByUserEmail(userEmail);

        User user = userOptional.orElseThrow(() -> {

            logger.warn("User not found with email: {}", userEmail);

            return new ResourceNotFoundException(
                    "USER NOT FOUND!"
            );
        });

        logger.debug("User found with email: {}", userEmail);

        return entityToDto(user);
    }


    // =========================
    // SEARCH USER
    // =========================

    @Override
    public List<userDto> searchUser(String keyword) {

        logger.info("Searching users with keyword: {}", keyword);

        List<User> foundUsers =
                userRepository.findByUserNameContaining(keyword);

        logger.info(
                "User search completed. Found {} users for keyword: {}",
                foundUsers.size(),
                keyword
        );

        return foundUsers.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }


    // =========================
    // GET ALL USERS
    // =========================

    @Override
    public PageResopnse<userDto> getAllUsers(
            int pagenumber,
            int pagesize,
            String sortby,
            String order) {

        logger.info(
                "Fetching users. Page: {}, Size: {}, SortBy: {}, Order: {}",
                pagenumber,
                pagesize,
                sortby,
                order
        );

        Sort sorted = order.equalsIgnoreCase("desc")
                ? Sort.by(sortby).descending()
                : Sort.by(sortby).ascending();

        Pageable pageable =
                PageRequest.of(pagenumber, pagesize, sorted);

        Page<User> page =
                userRepository.findAll(pageable);

        logger.info(
                "Fetched {} users from page {}. Total users: {}",
                page.getNumberOfElements(),
                page.getNumber(),
                page.getTotalElements()
        );

        return helper.getPageResponse(
                page,
                userDto.class
        );
    }


    // =========================
    // SAVE PROFILE IMAGE
    // =========================

    @Override
    public String saveUserProfileImage(
            String userid,
            String userImage) {

        logger.info(
                "Saving profile image '{}' for user '{}'",
                userImage,
                userid
        );

        User user = userRepository.findById(userid)
                .orElseThrow(() -> {
                    logger.warn(
                            "Profile image update failed. User not found with id: {}",
                            userid
                    );

                    return new ResourceNotFoundException(
                            "USER NOT FOUND!"
                    );
                });

        user.setProfileImage(userImage);

        userRepository.save(user);

        logger.info(
                "Profile image '{}' saved successfully for user '{}'",
                userImage,
                userid
        );

        return userImage;
    }


    // =========================
    // GET PROFILE IMAGE NAME
    // =========================

    @Override
    public String getProfileImagename(String userid) {

        logger.info(
                "Fetching profile image name for user: {}",
                userid
        );

        User user = userRepository.findById(userid)
                .orElseThrow(() -> {
                    logger.warn(
                            "Profile image fetch failed. User not found with id: {}",
                            userid
                    );

                    return new ResourceNotFoundException(
                            "USER NOT FOUND!"
                    );
                });

        String imageName = user.getProfileImage();

        logger.debug(
                "Profile image for user '{}' is '{}'",
                userid,
                imageName
        );

        return imageName;
    }


    // =========================
    // DTO → ENTITY
    // =========================
    private User dtoToEntity(userDto userdto) {

        logger.debug(
                "Converting user DTO to User entity"
        );

        return mapper.map(userdto, User.class);
    }
    // =========================
    // ENTITY → DTO
    // =========================

    private userDto entityToDto(User savedUser) {

        logger.debug(
                "Converting User entity to user DTO"
        );

        return mapper.map(savedUser, userDto.class);
    }
}