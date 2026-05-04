package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Page<Building> findByWorkspaceId(long workspaceId, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM building
            WHERE workspace_id = :workspaceId
              AND (
                    COALESCE(:keyword, '') = ''
                    OR CAST(building_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR COALESCE(name, '') ILIKE '%' || :keyword || '%'
                    OR COALESCE(address, '') ILIKE '%' || :keyword || '%'
                  )
            ORDER BY building_id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Building> searchByKeyword(@Param("workspaceId") long workspaceId,
                                   @Param("keyword") String keyword,
                                   @Param("limit") int limit,
                                   @Param("offset") int offset);
}
