package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.dto.LoginDto;
import com.pokemonreview.api.dto.RegisterDto;
import com.pokemonreview.api.models.Language;
import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.LanguageRepository;
import com.pokemonreview.api.repository.RoleRepository;
import com.pokemonreview.api.repository.UserRepository;
import com.pokemonreview.api.security.JWTGenerator;
import com.pokemonreview.api.service.FacebookOAuthService;
import com.pokemonreview.api.service.GoogleOAuthService;
import com.pokemonreview.api.service.TemplateService;
import com.pokemonreview.api.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;
    private final GoogleOAuthService googleOAuthService;
    private final FacebookOAuthService facebookOAuthService;
    private ValidatorService validatorService;
    private TemplateService templateService;
    private LanguageRepository languageRepository;

    @Autowired
    public AuthController(FacebookOAuthService facebookOAuthService, LanguageRepository languageRepository, TemplateService templateService, ValidatorService validatorService, GoogleOAuthService googleOAuthService, AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.googleOAuthService = googleOAuthService;
        this.validatorService = validatorService;
        this.templateService = templateService;
        this.languageRepository= languageRepository;
        this.facebookOAuthService = facebookOAuthService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody String loginJson) {
        try {
            // Validate the JSON against the schema
            Set<ValidationMessage> errors = validatorService.validate(loginJson);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }

            // Convert JSON string to LoginDto
            LoginDto loginDto = new ObjectMapper().readValue(loginJson, LoginDto.class);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Pair<String, Date> token = jwtGenerator.generateToken(authentication);
            UserEntity user = userRepository.findByUsername(loginDto.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setToken(token.getFirst());
            user.setFcm_token(loginDto.getFcm_token());
            userRepository.save(user);
            long date = token.getSecond().getTime();
            Map<String, Object> model = new HashMap<>();
            model.put("accessToken", token.getFirst());
            model.put("expireDate", date);
            JsonNode jsonResponse = templateService.generateJsonFromTemplate("responseLogin.ftl", model);

            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
//            return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        Language language = new Language();
        language.setLanguage("vn");
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));
        user.setEmail(registerDto.getEmail());
        user.setLanguage(language);
        userRepository.save(user);
        language.setUser(user);
        languageRepository.save(language);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }

    @GetMapping("/google")
    public RedirectView googleOAuth(@RequestParam String code) throws Exception {
        JsonNode tokenResponse = googleOAuthService.getOauthGoogleToken(code);
        String idToken = tokenResponse.get("id_token").asText();
        String aToken = tokenResponse.get("access_token").asText();

        JsonNode googleUser = googleOAuthService.getGoogleUser(idToken, aToken);
        if (!googleUser.get("verified_email").asBoolean()) {
            throw new Exception("Google email not verified");
        }

        String email = googleUser.get("email").asText();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            UserEntity nUser = new UserEntity();
            nUser.setEmail(email);
            nUser.setUsername(email);
            nUser.setPassword(passwordEncoder.encode("123456"));
            UserEntity sUser = userRepository.save(nUser);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            sUser.getUsername(),
                            "123456"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Pair<String, Date> token = jwtGenerator.generateToken(authentication);
            return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                       "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Pair<String, Date> token = jwtGenerator.generateToken(authentication);
        System.out.println(token);
        return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
    }

    @GetMapping("/facebook")
    public RedirectView facebookOAuth(@RequestParam String code) throws Exception {
        JsonNode tokenResponse = facebookOAuthService.getOauthFacebookToken(code);
        String accessToken = tokenResponse.get("access_token").asText();

        JsonNode facebookUser = facebookOAuthService.getFacebookUser(accessToken);
        if (facebookUser.get("email") == null) {
            throw new Exception("Facebook email not available");
        }

        String email = facebookUser.get("email").asText();
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Tạo người dùng mới nếu chưa có
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setUsername(facebookUser.get("name").asText()); // Sử dụng tên người dùng từ Facebook
            newUser.setPassword(passwordEncoder.encode("123456")); // Mật khẩu mặc định
            UserEntity savedUser = userRepository.save(newUser);

            // Xác thực người dùng mới
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            savedUser.getUsername(),
                            "123456" // Dùng mật khẩu mặc định để xác thực
                    ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Pair<String, Date> token = jwtGenerator.generateToken(authentication);

            return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
        }

        // Nếu người dùng đã tồn tại, xác thực và tạo JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        "123456" // Dùng mật khẩu đã mã hóa
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Pair<String, Date> token = jwtGenerator.generateToken(authentication);

        // Chuyển hướng đến ứng dụng client với access token
        return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
    }


}
