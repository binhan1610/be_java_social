package com.pokemonreview.api.controllers;

import com.pokemonreview.api.firebase.FirebaseService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") List<MultipartFile> files) {
        JSONArray jsonArray = new JSONArray();
        for(MultipartFile file : files) {
            String fileUrl = firebaseService.upload(file);
            jsonArray.put(fileUrl);
        }
        if (!jsonArray.isEmpty()) {
            return ResponseEntity.ok(jsonArray.toString());
        } else {
            return ResponseEntity.status(500).body("Upload failed");
        }
    }
}
