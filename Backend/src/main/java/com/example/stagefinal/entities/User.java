package com.example.stagefinal.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String fullname;

    @Column(nullable = true)
    private String email;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    private int points;

    @OneToMany(mappedBy = "user") // Establish the inverse side of the relationship
    @JsonManagedReference // Add this annotation

    private List<SocialPage> socialPages;

    @Enumerated(EnumType.STRING)
    private UserRole role;

}
