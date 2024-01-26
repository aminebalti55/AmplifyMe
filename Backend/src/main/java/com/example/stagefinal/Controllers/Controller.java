package com.example.stagefinal.Controllers;

import com.example.stagefinal.Repositories.UserRepository;
import com.example.stagefinal.configuration.JwtAuthenticationResponse;
import com.example.stagefinal.configuration.JwtTokenUtil;
import com.example.stagefinal.entities.*;
import com.example.stagefinal.services.GoogleAuthService;
import com.example.stagefinal.services.LinkedinApI;
import com.example.stagefinal.services.PasswordEncryptionService;
import com.example.stagefinal.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(path = "/login")
public class Controller {
    @Autowired
    UserService userService;
    @Autowired
     GoogleAuthService googleAuthService;
    @Autowired
    LinkedinApI linkedinApI;
@Autowired
    UserRepository userRepository;

  @Autowired
    PasswordEncryptionService passwordEncryptionService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @GetMapping("/hello")
    public String Welcome(){
        return"hello";
    }

    @GetMapping("/facebook/Authorization")
    public ResponseEntity<String> AuthorizationUrl() {
        String authUrl = userService.getAuthorizationUrl();
        return ResponseEntity.ok(authUrl);
    }

    @GetMapping("/facebook/userProfile")
    public ResponseEntity<String> getUserProfile(@RequestParam("access_token") String accessToken) {
        String userProfileUrl = "https://graph.facebook.com/me" +
                "?fields=id,email,first_name,last_name" +
                "&access_token=" + accessToken;

        ResponseEntity<String> response = new RestTemplate().getForEntity(userProfileUrl, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(response.getBody());
        } else {
            // Handle error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user profile.");
        }
    }
    @GetMapping("/google/userProfile")
    public ResponseEntity<String> getGoogleUserProfile(@RequestParam("access_token") String accessToken) {
        try {
            String userProfileUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = new RestTemplate().exchange(userProfileUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(response.getBody());
            } else {
                // Handle error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user profile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user profile.");
        }
    }

    @GetMapping("/facebook/callback")
    public ResponseEntity<String> facebookCallback(@RequestParam("code") String code) {
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization code is missing.");
        }

        String accessToken = userService.getAccessToken(code);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to obtain access token.");
        }

        System.out.println("Access Token: " + accessToken); // Print access token to console


        String userProfile = userService.getUserProfile(accessToken);

        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user profile.");
        }



        // Convert the user profile JSON to a User entity for Facebook
        User newUser = convertUserProfileToUserfacebook(userProfile);

        if (newUser == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to convert user profile to User entity.");
        }

        List<User> users = userService.getUsersByProviderId(newUser.getProviderId());
        User existingUser = users.isEmpty() ? null : users.get(0);

        if (existingUser != null) {

            userService.updateUser(existingUser, newUser);
        } else {
            userService.saveUser(newUser);
        }

        // Process user profile and create user session/token if needed
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:4200/homeSignedup") // Adjust the frontend URL
                .build();
    }


    private User convertUserProfileToUserfacebook(String userProfileJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(userProfileJson);
            String id = jsonNode.get("id").asText();
            String email = jsonNode.get("email").asText();
            String firstName = jsonNode.get("first_name").asText();
            String lastName = jsonNode.get("last_name").asText();
            String username = firstName + " " + lastName; // Combine first name and last name
            String imageUrl = "https://graph.facebook.com/" + id + "/picture?type=large"; // Construct the image URL

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setImageUrl(imageUrl);
            user.setPassword(generateRandomPassword()); // Generate a random password or use a secure mechanism
            user.setProvider(AuthProvider.facebook);
            user.setProviderId(id);

            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/linkedin/login")
    public ResponseEntity<String> linkedInLogin() {
        String authUrl = linkedinApI.getAuthorizationUrl();
        return ResponseEntity.ok(authUrl);
    }


    @GetMapping("/linkedin/callback")
    public ResponseEntity<String> linkedinCallback(@RequestParam("code") String code) {
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization code is missing.");
        }

        String accessToken = linkedinApI.getAccessToken(code);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to obtain access token.");
        }

        // Print the access token to the console if needed
        System.out.println("Access Token: " + accessToken);

        String userProfile = linkedinApI.getUserProfile(accessToken);

        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user profile.");
        }

        // Convert the user profile JSON to a User entity for LinkedIn
        User newUser = convertUserProfileToUserLinkedIn(userProfile, "linkedin");

        List<User> users = userService.getUsersByProviderId(newUser.getProviderId());
        User existingUser = users.isEmpty() ? null : users.get(0);

        if (existingUser != null) {
            // Update the existing user's information
            userService.updateUser(existingUser, newUser);
        } else {
            // Save the new user to the database
            userService.saveUser(newUser);
        }

        // Process user profile and create user session/token if needed
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:4200/homeSignedup") // Adjust the frontend URL
                .build();
    }

    private User convertUserProfileToUserLinkedIn(String userProfileJson, String provider) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(userProfileJson);
            String firstName = jsonNode.get("firstName").get("localized").get("fr_FR").asText();
            String lastName = jsonNode.get("lastName").get("localized").get("fr_FR").asText();
            String profilePictureUrl = jsonNode.get("profilePicture")
                    .get("displayImage~")
                    .get("elements")
                    .get(0)
                    .get("identifiers")
                    .get(0)
                    .get("identifier")
                    .asText();
          //  String email = jsonNode.get("emailAddress").asText(); // Extract email from the response

            User user = new User();
            user.setUsername(firstName + " " + lastName); // Combine first and last name as username
            user.setImageUrl(profilePictureUrl);
            user.setPassword(generateRandomPassword()); // Generate a random password or use a secure mechanism
            user.setProvider(AuthProvider.linkedIn);
            user.setProviderId(profilePictureUrl); // You might want to store the picture URL as the providerId
          //   user.setEmail(email); // Set the email

            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @GetMapping("/google/login")
    public ResponseEntity<String> googleLogin() {
        String authUrl = googleAuthService.getAuthorizationUrl();
        return ResponseEntity.ok(authUrl);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<String> googleCallback(@RequestParam("code") String code) {
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization code is missing.");
        }

        String accessToken = googleAuthService.getAccessToken(code);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to obtain access token.");
        }

        // You can print the access token to the console if needed
        System.out.println("Access Token: " + accessToken);

        String userProfile = googleAuthService.getUserProfile(accessToken);

        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user profile.");
        }

        // Convert the user profile JSON to a User entity
        User newUser = convertUserProfileToUser(userProfile, "google");

        List<User> usersg = userService.getUsersByProviderId(newUser.getProviderId());
        User existingUser = usersg.isEmpty() ? null : usersg.get(0);

        if (existingUser != null) {
            // Update the existing user's information
            userService.updateUser(existingUser, newUser);
        } else {
            // Save the new user to the database
            userService.saveUser(newUser);
        }

        // Redirect to the appropriate frontend URL after processing
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:4200/homeSignedup") // Adjust the frontend URL
                .build();
    }

    private User convertUserProfileToUser(String userProfileJson, String provider) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(userProfileJson);
            String email = jsonNode.get("email").asText();
            String username = jsonNode.get("name").asText(); // Use Google-provided username
            String imageUrl = jsonNode.get("picture").asText();

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setImageUrl(imageUrl);
            user.setPassword(generateRandomPassword()); // Generate a random password or use a secure mechanism
            user.setProvider(AuthProvider.google);
            user.setProviderId(username);

            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String generateRandomPassword() {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder newPassword = new StringBuilder();

        for (int i = 0; i < 12; i++) { // Generate a 12-character password
            int randomIndex = secureRandom.nextInt(characters.length());
            newPassword.append(characters.charAt(randomIndex));
        }

        return newPassword.toString();
    }

   @PostMapping("/normalLogin")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        LoginResponse loginResponse = new LoginResponse();
        boolean isAuthenticated = false;

        if (user != null) {
            String encryptedPassword = user.getPassword();
            String decryptedPassword = passwordEncryptionService.decrypt(encryptedPassword, request.getPassword());
            isAuthenticated = decryptedPassword != null;
        }

        if (isAuthenticated) {
            loginResponse.setAuthenticated(true);
            loginResponse.setUser(user);
           // String token = jwtTokenUtil.generateToken(user.getUsername());
           // loginResponse.setToken(token);

          if (user.getRole() == UserRole.ADMIN) {
                loginResponse.setRole("ADMIN");
            } else if (user.getRole() == UserRole.USER) {
                loginResponse.setRole("USER");
            }

          System.out.println("Login Response: " + loginResponse);

            return ResponseEntity.ok(loginResponse);
        } else {
            loginResponse.setMessage("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
    }


   /* @PostMapping("/normalLogin")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest request , AuthenticationManager authenticationManager,
                                                           JwtTokenUtil tokenProvider) {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Include only necessary information in the response
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

        }
*/

    @PostMapping("/signup")
    public ResponseEntity<User> signUpUser(@RequestBody User userDTO) {
        // Perform validation and error handling as needed

        // Call the service method to add the user
        User savedUser = userService.addUser2(userDTO);

        // Return a success response with the saved user
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/{username}/add-points")
    public User addPointsToUser(@PathVariable String username, @RequestParam int pointsToAdd) {
        return userService.addPointsToUser(username, pointsToAdd);
    }
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private Set<String> revokedTokens = ConcurrentHashMap.newKeySet();



    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        // Check if the Authorization header is missing or doesn't contain a valid token
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "");

        if (revokedTokens.contains(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token already revoked");
        }

        System.out.println("Logged out token: " + token);

        // Add the token to the blacklist
        revokedTokens.add(token);

        return ResponseEntity.ok("Logged out successfully");
    }


    @GetMapping("/getUserById/{id}")
    public ResponseEntity<User> GetUserByID(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}



