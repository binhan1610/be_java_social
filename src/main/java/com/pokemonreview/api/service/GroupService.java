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
    public long getGroupId() throws Exception {
        return IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.GROUP);
    }

    // Tạo nhóm
    public GroupUserEntity createGroup(GroupDto dto, long  userId) throws Exception {
        long timeStamp = new Date().getTime();
        GroupUserEntity group = new GroupUserEntity();
        group.setGroupId(getGroupId());
        group.setUserId(userId);
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
        long timeStamp = new Date().getTime();
        GroupEntity entity = new GroupEntity();
        entity.setGroupId(groupUserId);
        entity.setId(userId); // ID này là ID user
        entity.setStatus(1); // đang tham gia
        entity.setCreateTime(timeStamp);
        entity.setUpdatedTime(timeStamp);
        GroupUserEntity groupUser = groupUserRepository.findById(userId).orElse(null);
        if (groupUser != null) {
            int totalMember = groupUser.getTotalMember();
            totalMember += 1;
            groupUser.setTotalMember(totalMember);
            groupUser.setUpdatedTime(timeStamp);
            groupUserRepository.save(groupUser);
        }
        return groupRepository.save(entity);
    }

    // Lấy danh sách nhóm theo userId
    public List<GroupEntity> getGroupsByUserId(long userId) {
        return groupRepository.findById(userId).stream().toList();
    }

    // Sửa thông tin nhóm
    public Optional<GroupUserEntity> updateGroup(long id, GroupDto newData) {
        return groupUserRepository.findById(id).map(group -> {
            boolean updated = false;

            if (newData.getName() != null && !newData.getName().equals(group.getName())) {
                group.setName(newData.getName());
                updated = true;
            }

            if (newData.getDescription() != null && !newData.getDescription().equals(group.getDescription())) {
                group.setDescription(newData.getDescription());
                updated = true;
            }

            if (newData.getGroupAvatar() != null && !newData.getGroupAvatar().equals(group.getGroupAvatar())) {
                group.setGroupAvatar(newData.getGroupAvatar());
                updated = true;
            }

            if (newData.getGroupBackground() != null && !newData.getGroupBackground().equals(group.getGroupBackground())) {
                group.setGroupBackground(newData.getGroupBackground());
                updated = true;
            }

            if (updated) {
                group.setUpdatedTime(System.currentTimeMillis());
                return groupUserRepository.save(group);
            }

            return group; // Trả lại bản gốc nếu không có gì thay đổi
        });
    }


    // Xóa nhóm
    public void deleteGroup(long groupId) {
        groupUserRepository.deleteById(groupId);
        groupRepository.findByGroupId(groupId).forEach(e -> groupRepository.deleteById(e.getId()));
    }
}
