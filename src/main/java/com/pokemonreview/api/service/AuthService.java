package com.pokemonreview.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.dto.LoginDto;
import com.pokemonreview.api.dto.RegisterDTO;
import com.pokemonreview.api.dto.UpdateProfileDto;
import com.pokemonreview.api.libs.AuthConstant;
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
    private final NotificationService notificationService;

    public AuthService(GoogleOAuthService googleOAuthService, FacebookOAuthService facebookOAuthService,
                       ValidatorService validatorService, AuthenticationManager authenticationManager, JWTGenerator jwtGenerator, TemplateService templateService,
                       UserRepository userRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
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

    public Map<String, Object> convertProfileToModel(ProfileEntity profile) {
        Map<String, Object> model = new HashMap<>();
        model.put("profileId", profile.getUserId());

        if (profile.getEmail() != null) {
            model.put("email", profile.getEmail());
        }
        if (profile.getPhoneNumber() != null) {
            model.put("phoneNumber", profile.getPhoneNumber());
        }
        if (profile.getFistName() != null) {
            model.put("firstName", profile.getFistName());
        }
        if (profile.getLastName() != null) {
            model.put("lastName", profile.getLastName());
        }
        if (profile.getAvatar() != null) {
            model.put("avatar", profile.getAvatar());
        }
        if (profile.getBirthDay() != null) {
            model.put("birthDay", profile.getBirthDay());
        }
        if (profile.getAddress() != null) {
            model.put("address", profile.getAddress());
        }
        if (profile.getSex() != null) {
            model.put("sex", profile.getSex());
        }
        return model;
    }


    public ResponseEntity<?> getProfileUser(String username) {
        try {
            // Lấy user từ username
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Lấy profileId từ user
            long profileId = user.getUserId();

            ProfileEntity profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            Map<String, Object> model = convertProfileToModel(profile);
            JsonNode jsonResponse = templateService.generateJsonFromTemplate("profile.ftl", model);
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving profile: " + e.getMessage());
        }
    }

    public void logOut(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setToken(null);
        userRepository.save(user);
    }

    public ResponseEntity<?> updateProfile(String username, String profileJson) {
        try {
            // Validate JSON against schema using DTO
            Set<ValidationMessage> errors = validatorService.validate("UpdateProfileValidator", profileJson);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }

            // Lấy user từ username
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Lấy profileId từ user
            long profileId = user.getUserId();

            // Kiểm tra profile có tồn tại không
            ProfileEntity existingProfile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            // Chuyển JSON sang ProfileUpdateDTO
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateProfileDto profileUpdateDTO = objectMapper.readValue(profileJson, UpdateProfileDto.class);

            // Cập nhật các trường trong profile nếu có thông tin trong DTO
            if (profileUpdateDTO.getEmail() != null && !profileUpdateDTO.getEmail().isEmpty() && !existingProfile.getEmail().equals(profileUpdateDTO.getEmail())) {
                if (profileRepository.findByEmail(profileUpdateDTO.getEmail()).isPresent()) {
                    return ResponseEntity.badRequest().body("Email already exists!");
                }
                existingProfile.setEmail(profileUpdateDTO.getEmail());
            }

            if (profileUpdateDTO.getPhoneNumber() != null && !profileUpdateDTO.getPhoneNumber().isEmpty() && !existingProfile.getPhoneNumber().equals(profileUpdateDTO.getPhoneNumber())) {
                if (profileRepository.findByPhoneNumber(profileUpdateDTO.getPhoneNumber()).isPresent()) {
                    return ResponseEntity.badRequest().body("Phone already exists!");
                }
                existingProfile.setPhoneNumber(profileUpdateDTO.getPhoneNumber());
            }
            boolean fullName = false;
            if (profileUpdateDTO.getFirstName() != null && !profileUpdateDTO.getFirstName().isEmpty()) {
                existingProfile.setFistName(profileUpdateDTO.getFirstName());
                fullName = true;
            }

            if (profileUpdateDTO.getLastName() != null && !profileUpdateDTO.getLastName().isEmpty()) {
                existingProfile.setLastName(profileUpdateDTO.getLastName());
                fullName = true;
            }
            if(fullName){
                existingProfile.setFullName(existingProfile.getFistName()+" "+ existingProfile.getLastName());
            }

            if (profileUpdateDTO.getAvatar() != null && !profileUpdateDTO.getAvatar().isEmpty()) {
                existingProfile.setAvatar(profileUpdateDTO.getAvatar());
            }

            if (profileUpdateDTO.getBirthDay() != null && !profileUpdateDTO.getBirthDay().isEmpty()) {
                existingProfile.setBirthDay(profileUpdateDTO.getBirthDay());
            }

            if (profileUpdateDTO.getAddress() != null && !profileUpdateDTO.getAddress().isEmpty()) {
                existingProfile.setAddress(profileUpdateDTO.getAddress());
            }

            if (profileUpdateDTO.getSex() != null && !profileUpdateDTO.getSex().isEmpty()) {
                existingProfile.setSex(profileUpdateDTO.getSex());
            }

            // Cập nhật thời gian
            existingProfile.setUpdatedTime(System.currentTimeMillis());

            // Lưu lại thông tin đã cập nhật
            profileRepository.save(existingProfile);

            // Tạo response model
            Map<String, Object> model = convertProfileToModel(existingProfile);

            // Generate JSON response from template
            JsonNode jsonResponse = templateService.generateJsonFromTemplate("profile.ftl", model);

            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating profile: " + e.getMessage());
        }
    }


    public ResponseEntity<?> registerUser(String registerJson) throws Exception {
        // Check if username or email already exists
        Set<ValidationMessage> errors = validatorService.validate("RegisterValidator", registerJson);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // Convert JSON string to LoginDto
        RegisterDTO registerRequestDTO = new ObjectMapper().readValue(registerJson, RegisterDTO.class);
        String username = registerRequestDTO.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }
        long timeStamp = new Date().getTime();
        // Create new ProfileEntity
        ProfileEntity profile = new ProfileEntity();
        profile.setUserId(getProfileId()); // Generate ID
        profile.setFistName(registerRequestDTO.getFirstName());
        profile.setLastName(registerRequestDTO.getLastName());
        profile.setFullName(registerRequestDTO.getFirstName() + " " + registerRequestDTO.getLastName());
        profile.setSex(registerRequestDTO.getSex());
        if (username.contains("@")) {
            if (profileRepository.findByEmail(username).isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists!");
            }
            profile.setEmail(username);
            profile.setPhoneNumber("");
        } else {
            if (profileRepository.findByPhoneNumber(username).isPresent()) {
                return ResponseEntity.badRequest().body("Phone already exists!");
            }
            profile.setPhoneNumber(username);
            profile.setEmail("");
        }
        profile.setCreateTime(timeStamp);
        profile.setUpdatedTime(timeStamp);
        // Save ProfileEntity
        profile = profileRepository.save(profile);

        // Create new UserEntity
        UserEntity user = new UserEntity();
        user.setUserId(profile.getUserId());
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword())); // Encode password
        user.setCreateTime(timeStamp);
        user.setUpdatedTime(timeStamp);
        // Save UserEntity
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?>
    login(String loginJson) {
        try {
            notificationService.sendNotification("Facebook", "Có tin nhắn mới", "cnBIhOSekI7Lo9su-FgPus:APA91bGXL5O2JVHU9TSHSpbJeIx30IAacQ31BfWKm-PLqhjc7ht33xBjl1hWu8b3PCswGxNNgmwUV6o_CECCNqxHQfIIsxiKkfXr6wwIheEmBOcTP4r142E");
            // Validate the JSON against the schema
            Set<ValidationMessage> errors = validatorService.validate("LoginValidator", loginJson);
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
            user.setFcmToken(loginDto.getFcm_token());
            userRepository.save(user);

            // Prepare response model
            long expireDate = token.getSecond().getTime();
            Map<String, Object> model = new HashMap<>();
            model.put("accessToken", token.getFirst());
            model.put("expireDate", expireDate);
            model.put("userId", user.getUserId());
            // Build JSON response using template
            JsonNode jsonResponse = templateService.generateJsonFromTemplate("responseLogin.ftl", model);

            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

//    public RedirectView googleOAuthLogin(String code) throws Exception {
//        // Lấy token từ Google OAuth
//        JsonNode tokenResponse = googleOAuthService.getOauthGoogleToken(code);
//        String idToken = tokenResponse.get("id_token").asText();
//        String aToken = tokenResponse.get("access_token").asText();
//
//        // Lấy thông tin người dùng từ Google
//        JsonNode googleUser = googleOAuthService.getGoogleUser(idToken, aToken);
//        if (!googleUser.get("verified_email").asBoolean()) {
//            throw new Exception("Google email not verified");
//        }
//
//        String email = googleUser.get("email").asText();
//        UserEntity user = userRepository.findByEmail(email).orElse(null);
//
//        // Kiểm tra nếu user chưa tồn tại, tạo mới
//        if (user == null) {
//            // Tạo mới ProfileEntity
//            ProfileEntity newProfile = new ProfileEntity();
//            newProfile.setUserId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.PROFILE)); // Sinh id
//            newProfile.setEmail(email);
//            newProfile.setFistName(googleUser.get("given_name").asText()); // Lấy first name từ Google
//            newProfile.setLastName(googleUser.get("family_name").asText()); // Lấy last name từ Google
//            newProfile.setAvatar(googleUser.get("picture").asText()); // Avatar từ Google
//            ProfileEntity savedProfile = profileRepository.save(newProfile); // Lưu profile vào database
//
//            // Tạo mới UserEntity liên kết với ProfileEntity
//            UserEntity newUser = new UserEntity();
//            newUser.setUserId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.USER)); // Sinh id
//            newUser.setUserId(savedProfile.getUserId()); // Liên kết user với profile
//            newUser.setUsername(email);
//            newUser.setPassword(passwordEncoder.encode(AuthConstant.DEFAULT_PASSWORD)); // Mật khẩu mặc định cho user mới
//            UserEntity savedUser = userRepository.save(newUser);
//
//            // Xác thực và tạo Token
//            Authentication authentication = authenticateUser(savedUser.getUsername());
//            Pair<String, Date> token = jwtGenerator.generateToken(authentication);
//            return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
//        }
//
//
//        // Nếu user đã tồn tại, tiến hành xác thực
//        Authentication authentication = authenticateUser(user.getUsername());
//        Pair<String, Date> token = jwtGenerator.generateToken(authentication);
//
//        // Trả về RedirectView với token
//        return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
//    }
//
//    public RedirectView facebookOAuthLogin(String code) throws Exception {
//        // Lấy token từ Facebook
//        JsonNode tokenResponse = facebookOAuthService.getOauthFacebookToken(code);
//        String accessToken = tokenResponse.get("access_token").asText();
//
//        // Lấy thông tin người dùng từ Facebook
//        JsonNode facebookUser = facebookOAuthService.getFacebookUser(accessToken);
//        if (facebookUser.get("email") == null) {
//            throw new Exception("Facebook email not available");
//        }
//
//        String email = facebookUser.get("email").asText();
//        UserEntity user = userRepository.findByEmail(email).orElse(null);
//
//        // Kiểm tra nếu user chưa tồn tại
//        if (user == null) {
//            long timeStamp = new Date().getTime();
//            // Tạo mới ProfileEntity
//            ProfileEntity newProfile = new ProfileEntity();
//            newProfile.setProfileId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.PROFILE)); // Sinh id
//            newProfile.setEmail(email);
//            newProfile.setFistName(facebookUser.get("first_name").asText());
//            newProfile.setLastName(facebookUser.get("last_name").asText());
//            newProfile.setAvatar(facebookUser.get("picture").get("data").get("url").asText());
//            newProfile.setCreateTime(timeStamp);
//            newProfile.setUpdatedTime(timeStamp);
//            ProfileEntity savedProfile = profileRepository.save(newProfile); // Lưu profile vào database
//
//            // Tạo mới UserEntity liên kết với ProfileEntity
//            UserEntity newUser = new UserEntity();
//            newUser.setAccountId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.USER)); // Sinh id
//            newUser.setProfileId(savedProfile.getProfileId()); // Liên kết user với profile
//            newUser.setUsername(facebookUser.get("name").asText()); // Sử dụng tên người dùng từ Facebook
//            newUser.setPassword(passwordEncoder.encode(AuthConstant.DEFAULT_PASSWORD)); // Mật khẩu mặc định
//            newUser.setCreateTime(timeStamp);
//            newUser.setUpdatedTime(timeStamp);
//
//
//            // Xác thực người dùng mới
//            Authentication authentication = authenticateUser(newUser.getUsername());
//            Pair<String, Date> token = jwtGenerator.generateToken(authentication);
//            newUser.setFcm_token(token.getFirst());
//            userRepository.save(newUser);
//            // Chuyển hướng với access token
//            return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
//        }
//
//        // Nếu user đã tồn tại, xác thực và tạo JWT token
//        Authentication authentication = authenticateUser(user.getUsername());
//        Pair<String, Date> token = jwtGenerator.generateToken(authentication);
//        user.setFcm_token(token.getFirst());
//        userRepository.save(user);
//        // Chuyển hướng đến ứng dụng client với access token
//        return new RedirectView("http://localhost:3000/login/oauth?access_token=" + token.getFirst());
//    }

    private Authentication authenticateUser(String username) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, AuthConstant.DEFAULT_PASSWORD));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}