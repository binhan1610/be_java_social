package com.pokemonreview.api.service;

import com.pokemonreview.api.dto.GroupDto;
import com.pokemonreview.api.models.GroupEntity;
import com.pokemonreview.api.models.GroupUserEntity;
import com.pokemonreview.api.repository.GroupRepository;
import com.pokemonreview.api.repository.GroupUserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;

    public GroupService(GroupRepository groupRepository, GroupUserRepository groupUserRepository) {
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
    }

    // Tạo nhóm
    public GroupUserEntity createGroup(GroupDto dto) {
        long timeStamp = new Date().getTime();
        GroupUserEntity group = new GroupUserEntity();
        group.setName(dto.getName());
        group.setGroupAvatar(dto.getGroupAvatar());
        group.setGroupBackground(dto.getGroupBackground());
        group.setDescription(dto.getDescription());
        group.setType(dto.getType());
        group.setTotalMember(0);
        group.setCreateTime(timeStamp);
        group.setUpdatedTime(timeStamp);
        return groupUserRepository.save(group);
    }

    // Mời user vào nhóm
    public GroupEntity inviteUserToGroup(long groupUserId, long userId) {
        GroupEntity entity = new GroupEntity();
        entity.setGroupId(groupUserId);
        entity.setId(userId); // ID này là ID user
        entity.setStatus(1); // đang tham gia
        entity.setCreateTime(System.currentTimeMillis());
        entity.setUpdatedTime(System.currentTimeMillis());
        return groupRepository.save(entity);
    }

    // Lấy danh sách nhóm theo userId
    public List<GroupEntity> getGroupsByUserId(long userId) {
        return groupRepository.findById(userId).stream().toList();
    }

    // Sửa thông tin nhóm
    public Optional<GroupUserEntity> updateGroup(long id, GroupUserEntity newData) {
        return groupUserRepository.findById(id).map(group -> {
            group.setName(newData.getName());
            group.setDescription(newData.getDescription());
            group.setGroupAvatar(newData.getGroupAvatar());
            group.setGroupBackground(newData.getGroupBackground());
            group.setType(newData.getType());
            group.setTotalMember(newData.getTotalMember());
            group.setUpdatedTime(System.currentTimeMillis());
            return groupUserRepository.save(group);
        });
    }

    // Xóa nhóm
    public void deleteGroup(long groupId) {
        groupUserRepository.deleteById(groupId);
        groupRepository.findByGroupId(groupId).forEach(e -> groupRepository.deleteById(e.getId()));
    }
}
