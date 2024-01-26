package com.example.stagefinal.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class LinkedinApI {


    @Value("${spring.security.oauth2.client.registration.linkedin.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.linkedin.clientSecret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.linkedin.redirectUri}")
    private String redirectUri;

    public String getAuthorizationUrl() {
        return "https://www.linkedin.com/oauth/v2/authorization" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=r_liteprofile%20r_emailaddress" +  // Include the r_emailaddress scope
                "&response_type=code";
    }



    public String getAccessToken(String code) {
        String tokenUrl = "https://www.linkedin.com/oauth/v2/accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = new RestTemplate().postForEntity(tokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().get("access_token").toString();
        } else {
            // Handle error
            return null;
        }
    }

    public String getUserProfile(String accessToken) {
        String userProfileUrl = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = new RestTemplate().exchange(userProfileUrl, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            // Handle error
            return null;
        }
    }
}
