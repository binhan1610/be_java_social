package com.pokemonreview.api.service;

import com.pokemonreview.api.dto.PostDto;
import com.pokemonreview.api.models.PostEntity;
import com.pokemonreview.api.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public long getPostId() throws Exception {
        return IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.POST);
    }

    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }

    public List<PostEntity> getPostById(long postId) {
        return postRepository.findById(postId);
    }

    public PostEntity createPost(long userId, PostDto postDto, long id) throws Exception {
        PostEntity post = new PostEntity();
        post.setPostId(getPostId());
        post.setId(id);
        post.setUserId(userId);
        post.setTags(postDto.getTags());
        post.setImages(postDto.getImages());
        post.setCaption(postDto.getCaption());
        long now = new Date().getTime();
        post.setCreateTime(now);
        post.setUpdatedTime(now);
        return postRepository.save(post);
    }

    public PostEntity updatePost(long postId, PostDto postDto) {
        PostEntity post = postRepository.findByPostId(postId);
        if (post != null) {
            post.setTags(postDto.getTags());
            post.setImages(postDto.getImages());
            post.setCaption(postDto.getCaption());
            post.setUpdatedTime(new Date().getTime());
            return postRepository.save(post);
        }
        return null;
    }

    public PostEntity deletePost(long postId) {
        PostEntity post = postRepository.findByPostId(postId);
        if (post != null) {
            postRepository.delete(post);
            return post;
        }
        else return null;
    }
}
