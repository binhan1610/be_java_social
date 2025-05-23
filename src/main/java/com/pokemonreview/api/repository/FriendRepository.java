package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

    FriendEntity findByIdAndUserId(long userId, long friendId);
    // Tìm danh sách bạn bè theo userId
    List<FriendEntity> findByUserId(long userId);

    // Tìm theo status
    List<FriendEntity> findByStatus(int status);

    // Tìm bạn bè theo userId và status
    List<FriendEntity> findByUserIdAndStatus(long userId, int status);

    List<FriendEntity> findByIdAndStatus(long id, int status);

}
