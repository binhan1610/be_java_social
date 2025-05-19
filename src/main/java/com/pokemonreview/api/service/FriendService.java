package com.pokemonreview.api.service;


import com.pokemonreview.api.models.FriendEntity;
import com.pokemonreview.api.repository.FriendRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FriendService {

    private final FriendRepository friendRepository;

    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    public ResponseEntity<?> addFriend(Long userId, Long friendId) {
        FriendEntity friendExists = friendRepository.findByIdAndUserId(userId, friendId);

        if (friendExists != null) {
            int status = friendExists.getStatus();
            switch (status) {
                case 2: // FRIEND
                    return ResponseEntity.ok("Đã là bạn bè");
                case 0: // BLOCKED
                    return ResponseEntity.ok("Bạn đã bị chặn");
                case 1: // INVITED
                    return ResponseEntity.ok("Đã gửi lời mời");
                default:
                    return ResponseEntity.badRequest().body("Trạng thái không hợp lệ");
            }
        }

        // Tạo lời mời mới
        long timestamp = new Date().getTime();
        FriendEntity friend = new FriendEntity();
        friend.setId(userId); // Hoặc dùng sequence/id generator
        friend.setUserId(friendId);
        friend.setStatus(ConstantService.FriendStatus.INVITED.getValue());
        friend.setCreateTime(timestamp);
        friend.setUpdatedTime(timestamp);

        friendRepository.save(friend);
        return ResponseEntity.ok("Đã gửi lời mời kết bạn");
    }

    public ResponseEntity<?> removeFriend(Long userId, Long friendId) {
        FriendEntity friend = friendRepository.findByIdAndUserId(userId, friendId);
        if (friend != null) {
            friendRepository.delete(friend);
            return ResponseEntity.ok("Đã hủy kết bạn");
        } else {
            return ResponseEntity.status(404).body("Không tồn tại mối quan hệ để hủy");
        }
    }

    public List<FriendEntity> getFriendList(Long userId) {
        return friendRepository.findByUserIdAndStatus(userId, ConstantService.FriendStatus.FRIEND.getValue());
    }
}
