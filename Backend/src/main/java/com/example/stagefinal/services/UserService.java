package com.example.stagefinal.services;

import com.example.stagefinal.Repositories.UserRepository;
import com.example.stagefinal.entities.User;
import com.example.stagefinal.entities.UserRole;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service

public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired

    private BCryptPasswordEncoder passwordEncoder;
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Value("${spring.security.oauth2.client.registration.facebook.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.facebook.clientSecret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.facebook.redirectUri}")
    private String redirectUri;

    public String getAuthorizationUrl() {
        return "https://www.facebook.com/v13.0/dialog/oauth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=email,public_profile,user_likes";
    }

    public String getAccessToken(String code) {
        String accessTokenUrl = "https://graph.facebook.com/v13.0/oauth/access_token" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&client_secret=" + clientSecret +
                "&code=" + code;

        ResponseEntity<String> response = new RestTemplate().getForEntity(accessTokenUrl, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(response.getBody());
            return json.getString("access_token");
        } else {
            // Handle error
            return null;
        }
    }

    public String getUserProfile(String accessToken) {
        String userProfileUrl = "https://graph.facebook.com/me" +
                "?fields=id,email,first_name,last_name" +
                "&access_token=" + accessToken;

        ResponseEntity<String> response = new RestTemplate().getForEntity(userProfileUrl, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            // Handle error
            return null;
        }
    }
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }


    public User addUser2(User userDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        System.out.println("Checking email existence for: " + userDTO.getEmail());

        // Check if email already exists
        boolean emailExists = userRepository.existsByEmail(userDTO.getEmail());
        System.out.println("Email exists: " + emailExists);

        if (emailExists) {
            throw new RuntimeException("Email already exists");
        }

        User dbUser = new User();
        dbUser.setFullname(userDTO.getFullname());
        dbUser.setUsername(userDTO.getUsername());
        dbUser.setEmail(userDTO.getEmail());

        // Set the default role to ROLE_USER
        dbUser.setRole(UserRole.USER);

        String password = userDTO.getPassword();
        if (password != null && !password.isEmpty()) {
            // Hash the password before saving
            dbUser.setPassword(passwordEncoder.encode(password));
        }

        System.out.println("Saving user: " + dbUser.toString());
        User savedUser = userRepository.save(dbUser);
        System.out.println("Saved user: " + savedUser.toString());

        return savedUser;
    }

    public void deleteUserById(int userId) {
        // Check if the user exists
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addPointsToUser(String username, int pointsToAdd) {
        // Retrieve the user by username
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Update the user's points
        int currentPoints = user.getPoints();
        user.setPoints(currentPoints + pointsToAdd);

        // Save the updated user
        User updatedUser = userRepository.save(user);

        return updatedUser;
    }


    public List<User> getUsersByProviderId(String providerId) {
        // Assuming you have a UserRepository interface with a custom query method
        return userRepository.findByProviderId(providerId);
    }



    public void updateUser(User existingUser, User newUser) {
        // Update the fields of the existingUser with the new user's data
        existingUser.setUsername(newUser.getUsername());
        existingUser.setEmail(newUser.getEmail());
        existingUser.setImageUrl(newUser.getImageUrl());

        // Save the updated user to the database
        userRepository.save(existingUser);
    }

    private final Set<String> blacklist = new HashSet<>();

    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
