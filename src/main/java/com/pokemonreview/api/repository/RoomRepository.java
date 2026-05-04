package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query(
            value = """
                    SELECT *
                    FROM room r
                    WHERE r.workspace_id = :workspaceId
                      AND (:buildingId IS NULL OR r.building_id = :buildingId)
                      AND (COALESCE(:status, '') = '' OR LOWER(COALESCE(r.status, '')) = LOWER(:status))
                      AND (
                            COALESCE(:q, '') = ''
                            OR CAST(r.room_id AS TEXT) ILIKE '%' || :q || '%'
                            OR COALESCE(r.name, '') ILIKE '%' || :q || '%'
                            OR COALESCE(r.status, '') ILIKE '%' || :q || '%'
                          )
                      AND (
                            :available IS NULL
                            OR (:available = TRUE AND NOT EXISTS (
                                SELECT 1 FROM contract c
                                WHERE c.room_id = r.room_id
                                  AND LOWER(COALESCE(c.status, '')) = 'active'
                            ))
                            OR (:available = FALSE AND EXISTS (
                                SELECT 1 FROM contract c
                                WHERE c.room_id = r.room_id
                                  AND LOWER(COALESCE(c.status, '')) = 'active'
                            ))
                          )
                    ORDER BY r.room_id DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM room r
                    WHERE r.workspace_id = :workspaceId
                      AND (:buildingId IS NULL OR r.building_id = :buildingId)
                      AND (COALESCE(:status, '') = '' OR LOWER(COALESCE(r.status, '')) = LOWER(:status))
                      AND (
                            COALESCE(:q, '') = ''
                            OR CAST(r.room_id AS TEXT) ILIKE '%' || :q || '%'
                            OR COALESCE(r.name, '') ILIKE '%' || :q || '%'
                            OR COALESCE(r.status, '') ILIKE '%' || :q || '%'
                          )
                      AND (
                            :available IS NULL
                            OR (:available = TRUE AND NOT EXISTS (
                                SELECT 1 FROM contract c
                                WHERE c.room_id = r.room_id
                                  AND LOWER(COALESCE(c.status, '')) = 'active'
                            ))
                            OR (:available = FALSE AND EXISTS (
                                SELECT 1 FROM contract c
                                WHERE c.room_id = r.room_id
                                  AND LOWER(COALESCE(c.status, '')) = 'active'
                            ))
                          )
                    """,
            nativeQuery = true
    )
    Page<Room> searchRooms(@Param("workspaceId") long workspaceId,
                           @Param("buildingId") Long buildingId,
                           @Param("status") String status,
                           @Param("q") String q,
                           @Param("available") Boolean available,
                           Pageable pageable);
}
