package com.pokemonreview.api.controller;

import com.pokemonreview.api.models.LikeEntity;
import com.pokemonreview.api.service.ConstantService;
import com.pokemonreview.api.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private ConstantService constantService;

    @PostMapping("/add")
    public ResponseEntity<LikeEntity> addLike(@RequestParam long rootId,
                                              @RequestParam int type)  throws Exception{
        long userId = constantService.getUserIdByUsername();
        LikeEntity like = likeService.addLike(userId, rootId, type);
        return ResponseEntity.ok(like);
    }

    @DeleteMapping("/{rootId}")
    public ResponseEntity<String> removeLike(@RequestParam long rootId) {
        long userId = constantService.getUserIdByUsername();
        likeService.removeLike(userId, rootId);
        return ResponseEntity.ok("Like removed successfully.");
    }
}
