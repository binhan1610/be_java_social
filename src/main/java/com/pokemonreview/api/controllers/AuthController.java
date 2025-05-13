package com.pokemonreview.api.controllers;

import com.pokemonreview.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private AuthService authService;


    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            authService.logOut(username);
            return ResponseEntity.ok("Logout successful.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error logging out: " + e.getMessage());
        }
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
