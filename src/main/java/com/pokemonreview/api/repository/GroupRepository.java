package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    // Tìm các group theo groupId
    List<GroupEntity> findByGroupId(long groupId);

    // Tìm theo status
    List<GroupEntity> findByStatus(int status);

    // Tìm theo groupId và status
    List<GroupEntity> findByGroupIdAndStatus(long groupId, int status);
}
