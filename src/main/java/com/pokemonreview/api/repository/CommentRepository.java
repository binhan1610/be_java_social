package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    void deleteByCommentId(long commentId);
}
