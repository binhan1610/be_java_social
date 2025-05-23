package com.pokemonreview.api.service;

import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ConstantService {

    private final UserRepository userRepository;

    public ConstantService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public long getUserIdByUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserId();
    }

    public enum FriendStatus {
        BLOCKED(0),
        INVITED(1),
        FRIEND(2);

        private final int value;

        FriendStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static FriendStatus fromValue(int value) {
            for (FriendStatus status : FriendStatus.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid FriendStatus value: " + value);
        }
    }
}
