package com.example.stagefinal.Repositories;

import com.example.stagefinal.entities.SocialPage;
import com.example.stagefinal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocialPageRepository extends JpaRepository<SocialPage, Integer> {
    List<SocialPage> findByUser(User user);
    List<SocialPage> findByUser_Id(int userId);

}