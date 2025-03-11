package com.pokemonreview.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FacebookOAuthService {

    @Value("${facebook.client.id}")
    private String clientId;

    @Value("${facebook.client.secret}")
    private String clientSecret;

    @Value("${facebook.redirect.uri}")
    private String redirectUri;

    public JsonNode getOauthFacebookToken(String code) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // Xây dựng phần body cho yêu cầu POST để lấy token từ Facebook
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // Gửi yêu cầu POST tới Facebook để lấy access token
        ResponseEntity<String> response = restTemplate.postForEntity("https://graph.facebook.com/v17.0/oauth/access_token", request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.getBody());
    }

    public JsonNode getFacebookUser(String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // Thiết lập headers với Bearer token để truy vấn thông tin người dùng
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Gửi yêu cầu GET tới Facebook Graph API để lấy thông tin người dùng
        ResponseEntity<String> response = restTemplate.exchange(
                "https://graph.facebook.com/me?fields=id,name,email",
                HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.getBody());
    }
}
