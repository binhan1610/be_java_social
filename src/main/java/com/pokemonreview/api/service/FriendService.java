package com.pokemonreview.api.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.dto.RegisterDTO;
import com.pokemonreview.api.dto.SearchDTO;
import com.pokemonreview.api.models.FriendEntity;
import com.pokemonreview.api.models.ProfileEntity;
import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.FriendRepository;
import com.pokemonreview.api.repository.ProfileRepository;
import com.pokemonreview.api.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final ProfileRepository profileRepository;
    private final ValidatorService validatorService ;
    private final UserRepository userRepository;

    public FriendService(UserRepository userRepository,FriendRepository friendRepository,
                         ProfileRepository profileRepository, ValidatorService validatorService) {
        this.profileRepository = profileRepository;
        this.friendRepository = friendRepository;
        this.validatorService = validatorService;
        this.userRepository = userRepository;
    }

    public String getFcmToken(long roomId, long userId){
        System.out.println(roomId+ " "+userId);
        FriendEntity friend = friendRepository.findByFriendId(roomId);
        if(friend != null){
            long friendId;
            if(friend.getUserId() != userId){
                friendId = friend.getUserId();
            }
            else friendId = friend.getId();
            UserEntity user = userRepository.findByUserId(friendId);
            if(user != null && user.getFcmToken()!= null) return user.getFcmToken();
        }
        return "";
    }

    public long getFriendId() throws Exception {
        return IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.FRIEND);
    }

    public ResponseEntity<?> addFriend(Long userId, Long friendId) throws Exception {
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
        friend.setFriendId(getFriendId());
        friend.setId(userId); // Hoặc dùng sequence/id generator
        friend.setUserId(friendId);
        friend.setStatus(ConstantService.FriendStatus.INVITED.getValue());
        friend.setCreateTime(timestamp);
        friend.setUpdatedTime(timestamp);

        friendRepository.save(friend);
        return ResponseEntity.ok("Đã gửi lời mời kết bạn");
    }

    public ResponseEntity<?> approvalFriend(Long userId, Long friendId) {
        FriendEntity friendExists = friendRepository.findByIdAndUserId(friendId, userId);
        if (friendExists == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy lời mời kết bạn");
        }
        friendExists.setStatus(ConstantService.FriendStatus.FRIEND.getValue());
        friendRepository.save(friendExists);

        return ResponseEntity.ok("Đã kết bạn");
    }

    public ResponseEntity<?> removeFriend(Long userId, Long friendId) {
        FriendEntity friend = friendRepository.findByIdAndUserId(userId, friendId);
        if (friend != null) {
            friendRepository.delete(friend);
            return ResponseEntity.ok("Đã hủy kết bạn");
        } else {
            FriendEntity friend1 = friendRepository.findByIdAndUserId(friendId, userId);
            if(friend1!= null){
                friendRepository.delete(friend1);
                return ResponseEntity.ok("Đã hủy kết bạn");
            }
            else {
                return ResponseEntity.status(404).body("Không tồn tại mối quan hệ để hủy");
            }
        }
    }

    public List<ProfileEntity> getInvitedFriendList(Long userId) {
        List<FriendEntity> invitedFriends = friendRepository.findByUserIdAndStatus(userId, ConstantService.FriendStatus.INVITED.getValue());
        List<ProfileEntity> invitedFriendProfiles = new ArrayList<>();
        if(!invitedFriends.isEmpty()) {
            for(FriendEntity friend : invitedFriends) {
                long id = friend.getId();
                invitedFriendProfiles.add(profileRepository.findById(id).get());
            }
        }
        return invitedFriendProfiles;
    }

    public String getFriendIdByUserId(long userId, long friendId) {
        long id = 0;
        FriendEntity friend = friendRepository.findByIdAndUserId(userId, friendId);
        if (friend != null) {
            id = friend.getFriendId() ;
        }
        FriendEntity friend1 = friendRepository.findByIdAndUserId(friendId, userId);
        if (friend1 != null) {
            id = friend1.getFriendId();
        }
        return String.valueOf(id);
    }

    public List<ProfileEntity> getFriendList(Long userId) {
        List<Long> friends = new ArrayList<>();
        List<FriendEntity> friendsByUserId = friendRepository.findByUserIdAndStatus(userId, ConstantService.FriendStatus.FRIEND.getValue());
        if(!friendsByUserId.isEmpty()) {
            for(FriendEntity friend : friendsByUserId) {
                long id = friend.getId();
                friends.add(id);
            }
        }
        List<FriendEntity> friendsById = friendRepository.findByIdAndStatus(userId, ConstantService.FriendStatus.FRIEND.getValue());
        if(!friendsById.isEmpty()) {
            for(FriendEntity friend : friendsById) {
                long id = friend.getUserId();
                friends.add(id);
            }
        }

        return profileRepository.findAllById(friends);
    }

    public List<ProfileEntity> search(Long userId, String keyword) {
        List<Long> friends = new ArrayList<>();
        List<FriendEntity> friendsByUserId = friendRepository.findByUserIdAndStatus(userId, ConstantService.FriendStatus.FRIEND.getValue());
        if(!friendsByUserId.isEmpty()) {
            for(FriendEntity friend : friendsByUserId) {
                long id = friend.getId();
                friends.add(id);
            }
        }
        List<FriendEntity> friendsById = friendRepository.findByIdAndStatus(userId, ConstantService.FriendStatus.FRIEND.getValue());
        if(!friendsById.isEmpty()) {
            for(FriendEntity friend : friendsById) {
                long id = friend.getUserId();
                friends.add(id);
            }
        }
        return profileRepository.searchByUserIdsAndFullNameLike(friends, keyword);
    }

    public List<ProfileEntity> searchByUserId(Long userId, String keyword) {
        return profileRepository.searchByFullNameLike(userId, keyword);
    }

    public List<Map<String, String>> convertObject(List<ProfileEntity> list) {
        List<Map<String, String>> result = new ArrayList<>();
        for (ProfileEntity profileEntity : list) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", String.valueOf(profileEntity.getUserId()));
            map.put("email", profileEntity.getEmail());
            map.put("phoneNumber", profileEntity.getPhoneNumber());
            map.put("fistName", profileEntity.getFistName());
            map.put("lastName", profileEntity.getLastName());
            map.put("fullName", profileEntity.getFullName());
            map.put("avatar", profileEntity.getAvatar());
            map.put("birthDay", profileEntity.getBirthDay());
            map.put("address", profileEntity.getAddress());
            map.put("sex", profileEntity.getSex());
            map.put("createTime", String.valueOf(profileEntity.getCreateTime()));
            map.put("updatedTime", String.valueOf(profileEntity.getUpdatedTime()));
            result.add(map);
        }
        return result;
    }

}
