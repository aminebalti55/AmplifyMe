package com.example.stagefinal.services;

import com.example.stagefinal.Repositories.SocialPageRepository;
import com.example.stagefinal.Repositories.UserRepository;
import com.example.stagefinal.entities.SocialMediaType;
import com.example.stagefinal.entities.SocialOperation;
import com.example.stagefinal.entities.SocialPage;
import com.example.stagefinal.entities.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Random;
@Service
public class SocialPageService {

    private static final String BASE_GRAPH_URL = "https://graph.facebook.com/v14.0/";
    @Autowired
    SocialPageRepository socialPageRepository;
    @Autowired
    private UserRepository userRepository;

     RestTemplate restTemplate;

    @Autowired
UserService userService;



    public SocialPage addSocialPage(SocialPage socialPage) {
        // Retrieve the user from the database
        User user = userRepository.findById(socialPage.getUser().getId()).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Check if the user has enough points to spend
        int userPoints = user.getPoints();
        int pointsToSpend = socialPage.getPointsToSpend();

        if (userPoints < pointsToSpend) {
            throw new IllegalArgumentException("Not enough points to spend");
        }

        // Deduct the points from the user's balance
        user.setPoints(userPoints - pointsToSpend);

        // Save the updated user entity
        userRepository.save(user);

        // Save the social page
        SocialPage savedSocialPage = socialPageRepository.save(socialPage);

        // For debugging purposes, you can print out some information
        System.out.println("Social Page added successfully!");
        System.out.println("User points after deduction: " + user.getPoints());

        return savedSocialPage;
    }

    public SocialPage updateSocialPage(int id, SocialPage updatedSocialPage) {
        SocialPage existingSocialPage = socialPageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SocialPage not found with id: " + id));

        // Check if the user has enough points
        User user = existingSocialPage.getUser(); // Assuming there's a relationship between SocialPage and User

        // Calculate the difference in pointsToSpend
        int pointsDifference = updatedSocialPage.getPointsToSpend() - existingSocialPage.getPointsToSpend();

        System.out.println("User's points before update: " + user.getPoints());
        System.out.println("Points difference: " + pointsDifference);

        if (pointsDifference >= 0) {
            // Deduct the points difference from the user's points
            user.setPoints(user.getPoints() - pointsDifference);

            System.out.println("User's points after deduction: " + user.getPoints());

            // Update the fields you want to change
            existingSocialPage.setPageLink(updatedSocialPage.getPageLink());
            existingSocialPage.setSocialMediaType(updatedSocialPage.getSocialMediaType());
            existingSocialPage.setSocialOperation(updatedSocialPage.getSocialOperation());
            existingSocialPage.setPointsToSpend(updatedSocialPage.getPointsToSpend());

            // Save the updated SocialPage
            socialPageRepository.save(existingSocialPage);

            // Save the updated User with deducted points
            userRepository.save(user);

            return existingSocialPage;
        } else {
            // If the new pointsToSpend is less than the previous value, add the difference back to the user's points
            int pointsToAdd = Math.abs(pointsDifference); // Calculate the absolute difference
            user.setPoints(user.getPoints() + pointsToAdd);

            System.out.println("User's points after addition: " + user.getPoints());

            // Update the fields you want to change
            existingSocialPage.setPageLink(updatedSocialPage.getPageLink());
            existingSocialPage.setSocialMediaType(updatedSocialPage.getSocialMediaType());
            existingSocialPage.setSocialOperation(updatedSocialPage.getSocialOperation());
            existingSocialPage.setPointsToSpend(updatedSocialPage.getPointsToSpend());

            // Save the updated SocialPage
            socialPageRepository.save(existingSocialPage);

            // Save the updated User with added points
            userRepository.save(user);

            return existingSocialPage;
        }
    }

    public void deleteSocialPage(int id) {
        SocialPage socialPage = socialPageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SocialPage not found with id: " + id));

        socialPageRepository.delete(socialPage);
    }

    public String getRandomFacebookPage() {
        List<SocialPage> allSocialPages = socialPageRepository.findAll();

        if (allSocialPages.isEmpty()) {
            return null;
        }

        Random random = new Random();
        SocialPage randomPage = allSocialPages.get(random.nextInt(allSocialPages.size()));
        return randomPage.getPageLink();
    }

    public boolean isPageLiked(String accessToken, int userId, String pageId) {
        String likedPagesUrl = "https://graph.facebook.com/" + userId + "/likes" +
                "?access_token=" + accessToken;

        ResponseEntity<String> response = new RestTemplate().getForEntity(likedPagesUrl, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONArray likedPagesArray = jsonResponse.getJSONArray("data");

                for (int i = 0; i < likedPagesArray.length(); i++) {
                    JSONObject page = likedPagesArray.getJSONObject(i);
                    if (page.getString("id").equals(pageId)) {
                        return true; // User has liked the page
                    }
                }
                return false; // User has not liked the page
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

 public boolean isPostLiked(String accessToken, String userId, String postId, String posterId) {
        String combinedPostId = posterId + "_" + postId;
        String postLikedUrl = "https://graph.facebook.com/" + combinedPostId + "/likes" +
                "?access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = restTemplate.getForEntity(postLikedUrl, String.class);
        } catch (RestClientException e) {
            // Handle the exception (e.g., log, throw custom exception)
            e.printStackTrace();
            return false;
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONArray likesArray = jsonResponse.getJSONArray("data");

                for (int i = 0; i < likesArray.length(); i++) {
                    JSONObject user = likesArray.getJSONObject(i);
                    if (user.getString("id").equals(userId)) {
                        return true; // User has liked the post
                    }
                }
                return false; // User has not liked the post
            } catch (JSONException e) {
                // Handle the exception (e.g., log, throw custom exception)
                e.printStackTrace();
                return false;
            }
        } else {
            // Handle non-success status code (e.g., log, throw custom exception)
            return false;
        }
    }

    public void rewardUserWithPoints(User user, int points) {
        int currentPoints = user.getPoints();
        user.setPoints(currentPoints + points);
        userRepository.save(user);
    }

    public List<SocialPage> getSocialPagesByUserId(int userId) {
        return socialPageRepository.findByUser_Id(userId);
    }

}
