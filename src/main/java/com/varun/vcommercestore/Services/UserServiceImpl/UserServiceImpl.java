package com.varun.vcommercestore.Services.UserServiceImpl;

import com.varun.vcommercestore.Models.User;
import com.varun.vcommercestore.Repositories.UserRepository;
import com.varun.vcommercestore.Services.UserServices;
import com.varun.vcommercestore.dtos.userDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;

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

    @Override
    public userDto createUser(userDto userdto) {
        userdto.setUserId(UUID.randomUUID().toString());
        User user = dtoToEntity(userdto);
        User savedUser = userRepository.save(user);
        userDto newDto =  entityToDto(savedUser);
        return newDto;
    }

    @Override
    public userDto updateUser(userDto userdto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("user not found exception"));

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
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found!"));
        userRepository.delete(user);
    }

    @Override
    public userDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("USER NOT FOUND!"));
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
    public List<userDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<userDto> userDtos = users.
                stream().
                map(this::entityToDto).collect(Collectors.toList());

        return userDtos;
    }

    private userDto entityToDto(User savedUser) {
        return mapper.map(savedUser,userDto.class);
    }

    private User dtoToEntity(userDto userdto) {
        return mapper.map(userdto,User.class);
    }

}
