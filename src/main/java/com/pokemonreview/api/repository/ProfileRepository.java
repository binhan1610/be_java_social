package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByPhoneNumber(String phoneNumber);

    List<Profile> findByUserIdIn(List<Long> ids);

    @Query("SELECT p FROM Profile p WHERE p.userId IN :userIds AND LOWER(p.fullName) LIKE LOWER(CONCAT('%', :key, '%'))")
    List<Profile> searchByUserIdsAndFullNameLike(@Param("userIds") List<Long> userIds,
                                                 @Param("key") String key);


    @Query("SELECT p FROM Profile p WHERE p.userId != :userId AND LOWER(p.fullName) LIKE LOWER(CONCAT('%', :key, '%'))")
    List<Profile> searchByFullNameLike(long userId,
                                       @Param("key") String key);
}