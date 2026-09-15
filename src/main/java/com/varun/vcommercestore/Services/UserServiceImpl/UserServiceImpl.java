package com.varun.vcommercestore.Services.UserServiceImpl;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.User;
import com.varun.vcommercestore.Repositories.UserRepository;
import com.varun.vcommercestore.Services.FileService;
import com.varun.vcommercestore.Services.UserServices;
import com.varun.vcommercestore.Utils.Helper;
import com.varun.vcommercestore.dtos.PageResopnse;
import com.varun.vcommercestore.dtos.userDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserServices {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    FileService fileService;

    @Autowired
    private Helper helper;

    @Value("${user.profile.image.path}")
    private String UserProfileImagePath;

    @Override
    public userDto createUser(userDto userdto) {
        if(userRepository.existsByUserEmail(userdto.getUserEmail())){
            throw new DataIntegrityViolationException(" account with this email already exists!");
        }
        if (userRepository.existsByUserName(userdto.getUserName())){
            throw new DataIntegrityViolationException("Account with Username already exists!");
        }
        userdto.setUserId(UUID.randomUUID().toString());
        if(userdto.getProfileImage()==null) {
            userdto.setProfileImage("defaultProfile.jpg");
        }
        User user = dtoToEntity(userdto);
        User savedUser = userRepository.save(user);
        userDto newDto =  entityToDto(savedUser);
        return newDto;
    }

    @Override
    public userDto updateUser(userDto userdto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("USER NOT FOUND!"));

        user.setUserName(userdto.getUserName());
        user.setUserName(userdto.getUserName());
        user.setUserEmail(userdto.getUserEmail());
        user.setUserPassword(userdto.getUserPassword());
        user.setUserGender(userdto.getUserGender());
        user.setUserAddress(userdto.getUserAddress());
        user.setUserAbout(userdto.getUserAbout());
        user.setProfileImage(userdto.getProfileImage());

        User UpdatesUser = userRepository.save(user);
        userDto updateddto = entityToDto(UpdatesUser);

        return updateddto;
    }

    @Override
    public void deleteUser(String userId) throws IOException {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("USER NOT FOUND!"));

        String imageName = user.getProfileImage();

        if (imageName != null && !imageName.equalsIgnoreCase("defaultProfile.jpg")) {
            fileService.deleteFile(
                    imageName,
                    UserProfileImagePath
            );
        }

        userRepository.delete(user);
    }

    @Override
    public userDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("USER NOT FOUND!") );
        userDto userdto = entityToDto(user);
        return userdto;
    }

    @Override
    public userDto getUserByEmail(String userEmail) {
        Optional<User> userOptional= userRepository.findByUserEmail(userEmail);
        User user = userOptional.orElseThrow(()->new RuntimeException("User ot Found"));
        return entityToDto(user);
    }

    @Override
    public List<userDto> searchUser(String keyword) {
        List<User> foundUsers = userRepository.findByUserNameContaining(keyword);
        List<userDto> foundUsersDto = foundUsers.stream().map(this::entityToDto).collect(Collectors.toList());
        return foundUsersDto;
    }

    @Override
    public PageResopnse<userDto> getAllUsers(int pagenumber, int pagesize, String sortby, String order) {
        Sort sorted = order
                .equalsIgnoreCase("desc") ?
                Sort.by(sortby).descending() :
                Sort.by(sortby).ascending();
        Pageable pageable= PageRequest.of(pagenumber, pagesize,sorted);
        Page<User> page = userRepository.findAll(pageable);
        return helper.getPageResponse(page,userDto.class);
    }

    @Override
    public String saveUserProfileImage(String userid, String userImage) {
        User user = userRepository.findById(userid).orElseThrow(()->new ResourceNotFoundException("USER NOT FOUND!"));
        user.setProfileImage(userImage);
        userRepository.save(user);
        return userImage;
    }

    @Override
    public String getProfileImagename(String userid) {
        User user = userRepository.findById(userid).orElseThrow(()->new ResourceNotFoundException("USER NOT FOUND!"));
        String name = user.getProfileImage();
        return name;
    }

    private userDto entityToDto(User savedUser) {
        return mapper.map(savedUser,userDto.class);
    }

    private User dtoToEntity(userDto userdto) {
        return mapper.map(userdto,User.class);
    }

}
