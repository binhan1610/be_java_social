package com.pokemonreview.api.controllers;

import com.pokemonreview.api.models.Language;
import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.LanguageRepository;
import com.pokemonreview.api.repository.UserRepository;
import com.pokemonreview.api.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("api/language")
public class LocalLanguageController {

    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private JWTGenerator jwtGenerator;
    @Autowired
    public LocalLanguageController(JWTGenerator jwtGenerator,UserRepository userRepository,LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
        this.userRepository = userRepository;
        this.jwtGenerator = jwtGenerator;
    }

//    @PostConstruct
//    public void initLanguage() {
//        // Check if language with ID 1 already exists
//        if (!languageRepository.existsById(1L)) {
//            Language defaultLanguage = new Language();
//            defaultLanguage.setId(1L);
//            defaultLanguage.setLanguage("vn");
//
//            languageRepository.save(defaultLanguage);
//            System.out.println("Initialized default language with ID 1.");
//        } else {
//            System.out.println("Language with ID 1 already exists. Skipping initialization.");
//        }
//    }

    @GetMapping("/update")
    public String updateLanguage(@RequestParam String language,@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));

        UserEntity user = userRepository.findByUsername(username).orElse(null);
        // Find language with ID 1
        Language languageLocal = user.getLanguage();

        // Update language
        languageLocal.setLanguage(language);
        languageRepository.save(languageLocal);

        return "Changed language to " + language;
    }

    @GetMapping("/get")
    public String getLanguage(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));

        UserEntity user = userRepository.findByUsername(username).orElse(null);
        // Find language with ID 1
        Language languageLocal = user.getLanguage();
        return "Current language is " + languageLocal.getLanguage();
    }
}