package com.pokemonreview.api.controllers;

import com.pokemonreview.api.models.FriendEntity;
import com.pokemonreview.api.models.ProfileEntity;
import com.pokemonreview.api.service.ConstantService;
import com.pokemonreview.api.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private ConstantService constantService;

    // 1. Gửi lời mời kết bạn
    @PostMapping("/add/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long friendId) {
        try {
            long userId = constantService.getUserIdByUsername();
            return friendService.addFriend(userId, friendId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/approval/{friendId}")
    public ResponseEntity<?> approvalFriend(@PathVariable Long friendId) {
        try {
            long userId = constantService.getUserIdByUsername();
            return friendService.approvalFriend(userId, friendId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // 2. Hủy kết bạn
    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long friendId) {
        try {
            long userId = constantService.getUserIdByUsername();
            return friendService.removeFriend(userId, friendId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // 3. Lấy danh sách bạn bè
    @GetMapping("/list")
    public ResponseEntity<?> getFriendList() {
        try {
            long userId = constantService.getUserIdByUsername();
            List<ProfileEntity> friends = friendService.getFriendList(userId);

            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/list/invited")
    public ResponseEntity<?> getInvitedFriendList() {
        try {
            long userId = constantService.getUserIdByUsername();
            List<ProfileEntity> friends = friendService.getInvitedFriendList(userId);

            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

//    @PostMapping("/search")
//    public ResponseEntity<?> searchFriend(@RequestBody String searchJson) {
//        try {
//            long userId = constantService.getUserIdByUsername();
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }
}
