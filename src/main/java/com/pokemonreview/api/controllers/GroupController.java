package com.pokemonreview.api.controller;

import com.pokemonreview.api.dto.GroupDto;
import com.pokemonreview.api.models.GroupEntity;
import com.pokemonreview.api.models.GroupUserEntity;
import com.pokemonreview.api.service.ConstantService;
import com.pokemonreview.api.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Autowired
    private final GroupService groupService;

    @Autowired
    private ConstantService constantService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // Tạo nhóm
    @PostMapping("/create")
    public ResponseEntity<GroupUserEntity> createGroup(@RequestBody GroupDto group) throws Exception {
        long userId = constantService.getUserIdByUsername();
        return ResponseEntity.ok(groupService.createGroup(group, userId));
    }

    // Mời user vào nhóm
    @PostMapping("/invite")
    public ResponseEntity<GroupEntity> inviteUser(@RequestParam long groupId, @RequestParam long userId) throws Exception {
        return ResponseEntity.ok(groupService.inviteUserToGroup(groupId, userId));
    }

    // Lấy danh sách nhóm của user
    @GetMapping("/user")
    public ResponseEntity<List<GroupEntity>> getGroupsByUser() {
        long userId = constantService.getUserIdByUsername();
        return ResponseEntity.ok(groupService.getGroupsByUserId(userId));
    }

    // Sửa thông tin nhóm
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupUserEntity> updateGroup(@PathVariable long groupId, @RequestBody GroupDto group) {
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
