package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findByEmail(String email);

    Optional<ProfileEntity> findByPhoneNumber(String phoneNumber);

    List<ProfileEntity> findByUserIdIn(List<Long> ids);

    @Query("SELECT p FROM ProfileEntity p WHERE p.userId IN :userIds AND LOWER(p.fullName) LIKE LOWER(CONCAT('%', :key, '%'))")
    List<ProfileEntity> searchByUserIdsAndFullNameLike(@Param("userIds") List<Long> userIds,
                                                       @Param("key") String key);


    @Query("SELECT p FROM ProfileEntity p WHERE p.userId != :userId AND LOWER(p.fullName) LIKE LOWER(CONCAT('%', :key, '%'))")
    List<ProfileEntity> searchByFullNameLike(long userId,
                                             @Param("key") String key);
}