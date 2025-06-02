package com.pokemonreview.api.controller;

import com.pokemonreview.api.dto.GroupDto;
import com.pokemonreview.api.models.GroupEntity;
import com.pokemonreview.api.models.GroupUserEntity;
import com.pokemonreview.api.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // Tạo nhóm
    @PostMapping("/create")
    public ResponseEntity<GroupUserEntity> createGroup(@RequestBody GroupDto group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    // Mời user vào nhóm
    @PostMapping("/invite")
    public ResponseEntity<GroupEntity> inviteUser(@RequestParam long groupId, @RequestParam long userId) {
        return ResponseEntity.ok(groupService.inviteUserToGroup(groupId, userId));
    }

    // Lấy danh sách nhóm của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupEntity>> getGroupsByUser(@PathVariable long userId) {
        return ResponseEntity.ok(groupService.getGroupsByUserId(userId));
    }

    // Sửa thông tin nhóm
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupUserEntity> updateGroup(@PathVariable long groupId, @RequestBody GroupUserEntity group) {
        return groupService.updateGroup(groupId, group)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Xóa nhóm
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }
}
