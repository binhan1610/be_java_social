package com.pokemonreview.api.firebase;


import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import io.jsonwebtoken.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseService {
    private static final String BUCKET_NAME = "storage-image-80802.appspot.com";

    private String uploadFile(File file, String fileName) throws Exception {
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);

        // Tạo metadata với token truy cập
        String token = UUID.randomUUID().toString();
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("media")
                .setMetadata(Map.of("firebaseStorageDownloadTokens", token))
                .build();

        InputStream inputStream = FirebaseService.class.getClassLoader().getResourceAsStream("firebase-config.json");
        if (inputStream == null) {
            throw new Exception("Firebase config file not found");
        }

        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Blob blob = storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        // Tạo URL có kèm token truy cập
        String downloadUrl = String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s",
                BUCKET_NAME,
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()),
                token
        );

        return downloadUrl;
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));

            File file = this.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(file, fileName);
            file.delete();
            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or throw a specific exception or return an error message
        }
    }

    public String sendOtp(String phoneNumber) {
        try {
            // Gửi OTP cho số điện thoại bằng Firebase
            FirebaseAuth.getInstance().createCustomToken(phoneNumber);
            return "OTP đã được gửi";
        } catch (FirebaseAuthException e) {
            return "Lỗi khi gửi OTP: " + e.getMessage();
        }
    }

    // Xác thực mã OTP
    public String verifyOtp(String otpCode) {
        try {
            // Xác thực OTP với Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(otpCode);
            return "Xác thực thành công: " + decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            return "OTP không hợp lệ hoặc đã hết hạn: " + e.getMessage();
        }
    }

}
