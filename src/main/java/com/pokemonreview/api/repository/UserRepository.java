package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByEmail(String email);
    @Query(" select  u from UserEntity u where u.fcm_token= :fcmToken")
    Optional<UserEntity> findByFcm_Token(String fcmToken);
}
