package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.GroupUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUserEntity, Long> {

    // Tìm theo type
    List<GroupUserEntity> findByType(int type);

    // Tìm theo tên chứa từ khóa
    List<GroupUserEntity> findByNameContainingIgnoreCase(String keyword);

    // Tìm theo tổng số thành viên
    List<GroupUserEntity> findByTotalMemberGreaterThanEqual(int minMembers);
}
