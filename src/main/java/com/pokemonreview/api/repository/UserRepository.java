package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    UserEntity findByUserId(long userId);

    @Query("SELECT u FROM UserEntity u WHERE u.userId = (SELECT p.userId FROM ProfileEntity p WHERE p.email = :email)")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u WHERE u.fcmToken = :fcmToken")
    Optional<UserEntity> findByFcmToken(@Param("fcmToken") String fcmToken);

}