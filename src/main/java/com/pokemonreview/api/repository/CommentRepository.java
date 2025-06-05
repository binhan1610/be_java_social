package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    void deleteByCommentId(long commentId);

    List<CommentEntity> findAllByRootId(long postId);

    CommentEntity findByCommentId(long commentId);
}
