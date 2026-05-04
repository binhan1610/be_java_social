package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomServiceRepository extends JpaRepository<RoomService, Long> {
    List<RoomService> findByRoomId(long roomId);
    Page<RoomService> findByRoomId(long roomId, Pageable pageable);
    Page<RoomService> findByWorkspaceId(long workspaceId, Pageable pageable);
    void deleteByRoomIdAndServiceId(long roomId, long serviceId);

    @Query(value = """
            SELECT *
            FROM roomservice
            WHERE workspace_id = :workspaceId
              AND (
                    COALESCE(:keyword, '') = ''
                    OR CAST(room_service_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(room_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(service_id AS TEXT) ILIKE '%' || :keyword || '%'
                  )
            ORDER BY room_service_id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<RoomService> searchByKeyword(@Param("workspaceId") long workspaceId,
                                      @Param("keyword") String keyword,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);
}
