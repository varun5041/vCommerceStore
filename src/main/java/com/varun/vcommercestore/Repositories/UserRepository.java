package com.varun.vcommercestore.Repositories;

import com.varun.vcommercestore.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    //custom finders

    //1.FIND BY EMAIL
    Optional<User> findByUserEmail(String userEmail);

    List<User> findByUserNameContaining(String keyword);

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserName(String userName);

}
