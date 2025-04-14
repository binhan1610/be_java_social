package com.pokemonreview.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.dto.LoginDto;
import com.pokemonreview.api.dto.RegisterDTO;
import com.pokemonreview.api.models.ProfileEntity;
import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.ProfileRepository;
import com.pokemonreview.api.repository.UserRepository;
import com.pokemonreview.api.security.JWTGenerator;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private ValidatorService validatorService;
    private AuthenticationManager authenticationManager;
    private JWTGenerator jwtGenerator;
    private TemplateService templateService;
    private final GoogleOAuthService googleOAuthService;
    private final FacebookOAuthService facebookOAuthService;

    public AuthService(GoogleOAuthService googleOAuthService, FacebookOAuthService facebookOAuthService,
                       ValidatorService validatorService, AuthenticationManager authenticationManager, JWTGenerator jwtGenerator, TemplateService templateService,
                       UserRepository userRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.googleOAuthService = googleOAuthService;
        this.validatorService = validatorService;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.templateService = templateService;
        this.facebookOAuthService = facebookOAuthService;
    }

    public long getUserId() throws Exception {
        return IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.USER);
    }

    public long getProfileId() throws Exception {
        return IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.PROFILE);
    }


    public String registerUser(RegisterDTO registerRequestDTO) throws Exception {
        // Check if username or email already exists
        if (userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }
        if (profileRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists!");
        }

        // Create new ProfileEntity
        ProfileEntity profile = new ProfileEntity();
        profile.setProfileId(getProfileId()); // Generate ID
        profile.setEmail(registerRequestDTO.getEmail());
        profile.setPhoneNumber(registerRequestDTO.getPhoneNumber());
        profile.setFistName(registerRequestDTO.getFirstName());
        profile.setLastName(registerRequestDTO.getLastName());
        profile.setAvatar(registerRequestDTO.getAvatar());
        profile.setBirthDay(registerRequestDTO.getBirthDay());
        profile.setAddress(registerRequestDTO.getAddress());

        // Save ProfileEntity
        profile = profileRepository.save(profile);

        // Create new UserEntity
        UserEntity user = new UserEntity();
        user.setAccountId(getUserId()); // Generate ID
        user.setProfileId(profile.getProfileId()); // Link user to profile
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword())); // Encode password
        user.setFcm_token(registerRequestDTO.getFcmToken());
        user.setGoogleId(registerRequestDTO.getGoogleId());
        user.setFacebookId(registerRequestDTO.getFacebookId());

        // Save UserEntity
        userRepository.save(user);

        return "User registered successfully!";
    }

    public ResponseEntity<?> login(String loginJson) {
        try {
            // Validate the JSON against the schema
            Set<ValidationMessage> errors = validatorService.validate(loginJson);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }

            // Convert JSON string to LoginDto
            LoginDto loginDto = new ObjectMapper().readValue(loginJson, LoginDto.class);

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT Token
            Pair<String, Date> token = jwtGenerator.generateToken(authentication);

            // Update user information
            UserEntity user = userRepository.findByUsername(loginDto.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setToken(token.getFirst());
            user.setFcm_token(loginDto.getFcm_token());
            userRepository.save(user);

            // Prepare response model
            long expireDate = token.getSecond().getTime();
            Map<String, Object> model = new HashMap<>();
            model.put("accessToken", token.getFirst());
            model.put("expireDate", expireDate);

            // Build JSON response using template
            JsonNode jsonResponse = templateService.generateJsonFromTemplate("responseLogin.ftl", model);

            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    public RedirectView googleOAuthLogin(String code) throws Exception {
        // Lấy token từ Google OAuth
        JsonNode tokenResponse = googleOAuthService.getOauthGoogleToken(code);
        String idToken = tokenResponse.get("id_token").asText();
        String aToken = tokenResponse.get("access_token").asText();

        // Lấy thông tin người dùng từ Google
        JsonNode googleUser = googleOAuthService.getGoogleUser(idToken, aToken);
        if (!googleUser.get("verified_email").asBoolean()) {
            throw new Exception("Google email not verified");
        }

        String email = googleUser.get("email").asText();
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        // Kiểm tra nếu user chưa tồn tại, tạo mới
        if (user == null) {
            // Tạo mới ProfileEntity
            ProfileEntity newProfile = new ProfileEntity();
            newProfile.setProfileId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.PROFILE)); // Sinh id
            newProfile.setEmail(email);
            newProfile.setFistName(googleUser.get("given_name").asText()); // Lấy first name từ Google
            newProfile.setLastName(googleUser.get("family_name").asText()); // Lấy last name từ Google
            newProfile.setAvatar(googleUser.get("picture").asText()); // Avatar từ Google
            ProfileEntity savedProfile = profileRepository.save(newProfile); // Lưu profile vào database

            // Tạo mới UserEntity liên kết với ProfileEntity
            UserEntity newUser = new UserEntity();
            newUser.setAccountId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.USER)); // Sinh id
            newUser.setProfileId(savedProfile.getProfileId()); // Liên kết user với profile
            newUser.setUsername(email);
            newUser.setPassword(passwordEncoder.encode("123456")); // Mật khẩu mặc định cho user mới
            UserEntity savedUser = userRepository.save(newUser);

            // Xác thực và tạo Token
            Authentication authentication = authenticateUser(savedUser.getUsername(), "123456");
            Pair<String, Date> token = jwtGenerator.generateToken(authentication);
            return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
        }


        // Nếu user đã tồn tại, tiến hành xác thực
        Authentication authentication = authenticateUser(user.getUsername(), "123456");
        Pair<String, Date> token = jwtGenerator.generateToken(authentication);

        // Trả về RedirectView với token
        return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
    }

    public RedirectView facebookOAuthLogin(String code) throws Exception {
        // Lấy token từ Facebook
        JsonNode tokenResponse = facebookOAuthService.getOauthFacebookToken(code);
        String accessToken = tokenResponse.get("access_token").asText();

        // Lấy thông tin người dùng từ Facebook
        JsonNode facebookUser = facebookOAuthService.getFacebookUser(accessToken);
        if (facebookUser.get("email") == null) {
            throw new Exception("Facebook email not available");
        }

        String email = facebookUser.get("email").asText();
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        // Kiểm tra nếu user chưa tồn tại
        if (user == null) {
            // Tạo mới ProfileEntity
            ProfileEntity newProfile = new ProfileEntity();
            newProfile.setProfileId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.PROFILE)); // Sinh id
            newProfile.setEmail(email);
            newProfile.setFistName(facebookUser.get("first_name").asText());
            newProfile.setLastName(facebookUser.get("last_name").asText());
            newProfile.setAvatar(facebookUser.get("picture").get("data").get("url").asText()); // Avatar từ Facebook
            ProfileEntity savedProfile = profileRepository.save(newProfile); // Lưu profile vào database

            // Tạo mới UserEntity liên kết với ProfileEntity
            UserEntity newUser = new UserEntity();
            newUser.setAccountId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.USER)); // Sinh id
            newUser.setProfileId(savedProfile.getProfileId()); // Liên kết user với profile
            newUser.setUsername(facebookUser.get("name").asText()); // Sử dụng tên người dùng từ Facebook
            newUser.setPassword(passwordEncoder.encode("123456")); // Mật khẩu mặc định
            UserEntity savedUser = userRepository.save(newUser);

            // Xác thực người dùng mới
            Authentication authentication = authenticateUser(savedUser.getUsername(), "123456");
            Pair<String, Date> token = jwtGenerator.generateToken(authentication);

            // Chuyển hướng với access token
            return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
        }

        // Nếu user đã tồn tại, xác thực và tạo JWT token
        Authentication authentication = authenticateUser(user.getUsername(), "123456");
        Pair<String, Date> token = jwtGenerator.generateToken(authentication);

        // Chuyển hướng đến ứng dụng client với access token
        return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
    }

    private Authentication authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}