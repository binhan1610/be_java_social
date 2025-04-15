package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemonreview.api.dto.RegisterDTO;
import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.UserRepository;
import com.pokemonreview.api.security.JWTGenerator;
import com.pokemonreview.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private AuthService authService;


    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody String loginJson) {
        try {
            // Gửi logic xử lý tới AuthService
            return authService.login(loginJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody String registerJson) {
        try {
            return authService.registerUser(registerJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/google")
    public RedirectView googleOAuth(@RequestParam String code) throws Exception {
        return authService.googleOAuthLogin(code);
    }

    @GetMapping("/facebook")
    public RedirectView facebookOAuth(@RequestParam String code) throws Exception {
        return authService.facebookOAuthLogin(code);
    }


}
