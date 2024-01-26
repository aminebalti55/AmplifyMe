package com.example.stagefinal.Controllers;

import com.example.stagefinal.entities.SocialMediaType;
import com.example.stagefinal.entities.SocialOperation;
import com.example.stagefinal.entities.SocialPage;
import com.example.stagefinal.entities.User;
import com.example.stagefinal.services.SocialPageService;
import com.example.stagefinal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping(path = "/socialPage")
@CrossOrigin(origins = {"*"})

public class SocialPageController {

    @Autowired
    SocialPageService socialPageService;

    @Autowired
    UserService userService;


    @PostMapping("/add")
    public ResponseEntity<?> addSocialPage(@RequestBody SocialPage socialPage) {
        try {
            SocialPage savedSocialPage = socialPageService.addSocialPage(socialPage);
            return ResponseEntity.ok(savedSocialPage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SocialPage>> getSocialPagesByUserId(@PathVariable int userId) {
        List<SocialPage> socialPages = socialPageService.getSocialPagesByUserId(userId);
        return ResponseEntity.ok(socialPages);
    }

    @PutMapping("/Update/{id}")
    public ResponseEntity<?> updateSocialPage(@PathVariable int id, @RequestBody SocialPage updatedSocialPage) {
        try {
            SocialPage updatedPage = socialPageService.updateSocialPage(id, updatedSocialPage);
            return ResponseEntity.ok(updatedPage);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<Void> deleteSocialPage(@PathVariable int id) {
        socialPageService.deleteSocialPage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/random-page")
    public ResponseEntity<String> getRandomFacebookPage() {
        String randomPageLink = socialPageService.getRandomFacebookPage();

        if (randomPageLink == null) {
            return ResponseEntity.badRequest().body("No linked Facebook pages found.");
        }

        return ResponseEntity.ok(randomPageLink);
    }
    @GetMapping("/facebook/is-page-liked")
    public ResponseEntity<String> isPageLiked(
            @RequestParam("userId") int userId,
            @RequestParam("pageId") String pageId,
            @RequestParam("code") String code
    ) {
        String accessToken = userService.getAccessToken(code);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to obtain access token.");
        }

        boolean isLiked = socialPageService.isPageLiked(accessToken, userId, pageId);

        if (isLiked) {
            return ResponseEntity.ok("The page is liked by the user.");
        } else {
            return ResponseEntity.ok("The page is not liked by the user.");
        }
    }


    @GetMapping("/check-like-and-reward")
    public ResponseEntity<String> checkLikeAndReward(
            @RequestParam String accessToken,
            @RequestParam int userId,
            @RequestParam String pageId
    ) {
        User user = userService.getUserById(userId); // You need to implement this method

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (socialPageService.isPageLiked(accessToken, userId, pageId)) {
            socialPageService.rewardUserWithPoints(user, 5);
            return ResponseEntity.ok("User liked the page and was rewarded with 5 points.");
        } else {
            return ResponseEntity.ok("User has not liked the page.");
        }
    }


}
