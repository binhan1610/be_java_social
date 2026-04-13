package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Tìm phòng theo tòa nhà
    List<Room> findByBuildingId(long buildingId);

    // Tìm tất cả phòng trong một workspace
    List<Room> findByWorkspaceId(long workspaceId);
}