package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    LikeEntity findByUserIdAndRootId(long userId, long rootId);
    void deleteByUserIdAndRootId(long userId, long rootId);
}
