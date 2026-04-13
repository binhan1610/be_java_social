package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    User findByUserId(long userId);

    @Query("SELECT u FROM User u WHERE u.userId = (SELECT p.userId FROM Profile p WHERE p.email = :email)")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.fcmToken = :fcmToken")
    Optional<User> findByFcmToken(@Param("fcmToken") String fcmToken);

}