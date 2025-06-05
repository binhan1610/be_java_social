package com.pokemonreview.api.controllers;

import com.pokemonreview.api.dto.PostDto;
import com.pokemonreview.api.models.PostEntity;
import com.pokemonreview.api.service.ConstantService;
import com.pokemonreview.api.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private final PostService postService;

    @Autowired
    private ConstantService constantService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Get all posts
    @GetMapping("/all")
    public List<PostEntity> getAllPosts() throws Exception {
        long userId = constantService.getUserIdByUsername();
        return postService.getAllPosts(userId);
    }

    // Get post by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable long id) {
        List<PostEntity> post = postService.getPostById(id);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // Get post by user
    @GetMapping("")
    public ResponseEntity<?> getPostMyPost(@PathVariable long id) {
        long userId = constantService.getUserIdByUsername();
        List<PostEntity> post = postService.getPostById(userId);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // Create a new post
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody PostDto postDto) throws Exception {
        long userId = constantService.getUserIdByUsername();
        PostEntity post = postService.createPost(userId, postDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PostMapping("/{groupId}")
    public ResponseEntity<?> createPostGroup(@RequestBody PostDto postDto
                                             ,@PathVariable long groupId) throws Exception {
        long userId = constantService.getUserIdByUsername();
        PostEntity post = postService.createPost(userId, postDto, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }


    // Update a post
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable long id,
            @RequestBody PostDto postDto) {
        PostEntity updatedPost = postService.updatePost(id, postDto);
        return updatedPost != null ? ResponseEntity.ok(updatedPost) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable long id) {
        PostEntity updatedPost = postService.deletePost(id);
        return updatedPost != null ? ResponseEntity.ok(updatedPost) : ResponseEntity.notFound().build();
    }
}

