package com.pokemonreview.api.controllers;

import com.pokemonreview.api.dto.CommentDto;
import com.pokemonreview.api.models.CommentEntity;
import com.pokemonreview.api.service.CommentService;
import com.pokemonreview.api.service.ConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ConstantService constantService;

    @GetMapping("/{postId}")
    private ResponseEntity<?> getComment(@PathVariable long postId) throws Exception {
        long userId = constantService.getUserIdByUsername();
        return ResponseEntity.ok(commentService.getList(postId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentDto request) throws Exception{
        long userId = constantService.getUserIdByUsername();
        CommentEntity comment = commentService.addComment(
                userId,
                request.getRootId(),
                request.getTitle(),
                request.getImage()
        );
        return ResponseEntity.ok(comment);
    }


    @DeleteMapping("/remove")
    public ResponseEntity<String> removeComment(@RequestParam long commentId) {
        commentService.removeComment(commentId);
        return ResponseEntity.ok("Comment removed successfully.");
    }
}
