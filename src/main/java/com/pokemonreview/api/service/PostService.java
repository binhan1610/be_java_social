package com.pokemonreview.api.service;

import com.pokemonreview.api.dto.PostDto;
import com.pokemonreview.api.models.FriendEntity;
import com.pokemonreview.api.models.PostEntity;
import com.pokemonreview.api.models.ProfileEntity;
import com.pokemonreview.api.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FriendService friendService;

    @Autowired
    private GroupService groupService;

    public long getPostId() throws Exception {
        return IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.POST);
    }

    public List<PostEntity> getAllPosts(long userId) throws Exception {
        List<Long> listGroup = groupService.getMyGroup(userId);
        List<Long> ids = new ArrayList<>(listGroup);
        ids.add(userId);
        List<ProfileEntity> friendEntityList = friendService.getFriendList(userId);
        for(ProfileEntity profile:friendEntityList){
            ids.add(profile.getUserId());
        }
        return postRepository.getListPostInIds(ids);
    }

    public List<PostEntity> getPostById(long userId) {
        return postRepository.findById(userId);
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

    public List<Map<String, String>> convertPostEntityToMap(List<PostEntity> list) {
        List<Map<String, String>> result = new ArrayList<>();
        for (PostEntity post : list) {
            Map<String, String> map = new HashMap<>();
            map.put("postId", String.valueOf(post.getPostId()));
            map.put("userId", String.valueOf(post.getUserId()));
            map.put("id", String.valueOf(post.getId()));

            // Với list tags và images, convert thành chuỗi JSON hoặc chuỗi nối
            map.put("tags", post.getTags() != null ? String.join(",", post.getTags()) : null);
            map.put("images", post.getImages() != null ? String.join(",", post.getImages()) : null);

            map.put("caption", post.getCaption());
            map.put("createTime", String.valueOf(post.getCreateTime()));
            map.put("updatedTime", String.valueOf(post.getUpdatedTime()));

            result.add(map);
        }
        return result;
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
