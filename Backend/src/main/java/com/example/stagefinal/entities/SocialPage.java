package com.example.stagefinal.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class SocialPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne
    @JoinColumn(name = "user_id") // Specify the foreign key column name
    @JsonBackReference
    private User user;

    private String pageLink;


    @Enumerated(EnumType.STRING)
    private SocialMediaType socialMediaType;

    @Enumerated(EnumType.STRING)
    private SocialOperation socialOperation;

    private int pointsToSpend;

}
