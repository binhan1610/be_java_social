package com.pokemonreview.api.service;

import com.pokemonreview.api.models.LikeEntity;
import com.pokemonreview.api.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public long getLikeId() throws Exception {
        return IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.LIKE);
    }

    public Map<String, String> getCountByPostId(long postId, long userId,Map<String, String> map) throws Exception{
        List<LikeEntity> list = likeRepository.findByRootId(postId);
        int count = list.size();
        map.put("count_like",String.valueOf(count));
        LikeEntity like = likeRepository.findByUserIdAndRootId(userId, postId);
        map.put("is_like", like != null ? "true" : "false");
        return map;
    }

    public LikeEntity addLike(long userId, long rootId, int type) throws Exception {
        // Kiểm tra nếu like đã tồn tại thì trả lại
        LikeEntity like = likeRepository.findByUserIdAndRootId(userId, rootId);
        if(like == null){
            LikeEntity newLike = new LikeEntity();
            newLike.setUserId(userId);
            newLike.setRootId(rootId);
            newLike.setType(type);
            newLike.setLikeId(getLikeId());
            newLike.setCreateTime(System.currentTimeMillis());
            newLike.setUpdatedTime(System.currentTimeMillis());
            return likeRepository.save(newLike);
        }
        else{
            removeLike(userId, rootId);
            return null;
        }
    }

    public void removeLike(long userId, long rootId) {
        LikeEntity like = likeRepository.findByUserIdAndRootId(userId, rootId);
        if(like!=null){
            likeRepository.delete(like);
        }
    }
}
