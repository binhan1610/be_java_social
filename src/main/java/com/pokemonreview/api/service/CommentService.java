package com.pokemonreview.api.service;

import com.pokemonreview.api.models.CommentEntity;
import com.pokemonreview.api.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public long getCommentId() throws Exception {
        return IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.COMMENT);
    }

    public List<CommentEntity> getList(long rootId) throws Exception {
        return commentRepository.findAllByRootId(rootId);
    }

    public CommentEntity addComment(long userId, long rootId, String title, String image) throws Exception {
        CommentEntity comment = new CommentEntity();
        comment.setCommentId(getCommentId()); // hoặc UUID.randomUUID().getMostSignificantBits()
        comment.setUserId(userId);
        comment.setRootId(rootId);
        comment.setCommentId(getCommentId());
        comment.setTitle(title);
        comment.setImage(image);
        comment.setCreateTime(System.currentTimeMillis());
        comment.setUpdatedTime(System.currentTimeMillis());
        return commentRepository.save(comment);
    }

    public void removeComment(long commentId) {
        CommentEntity comment = commentRepository.findByCommentId(commentId);
        if(comment != null){
            commentRepository.delete(comment);
        }
    }
}
