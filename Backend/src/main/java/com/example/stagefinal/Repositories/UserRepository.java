package com.example.stagefinal.Repositories;

import com.example.stagefinal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {


    User findByUsername(String username);
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findByProviderId(String providerId);

}