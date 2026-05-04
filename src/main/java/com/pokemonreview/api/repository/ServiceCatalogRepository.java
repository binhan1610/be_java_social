package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.ServiceCatalog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {
    List<ServiceCatalog> findByWorkspaceId(long workspaceId);

    @Query(value = """
            SELECT *
            FROM service
            WHERE workspace_id = :workspaceId
              AND (
                    COALESCE(:keyword, '') = ''
                    OR CAST(service_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR COALESCE(name, '') ILIKE '%' || :keyword || '%'
                    OR COALESCE(unit, '') ILIKE '%' || :keyword || '%'
                  )
            ORDER BY service_id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<ServiceCatalog> searchByKeyword(@Param("workspaceId") long workspaceId,
                                         @Param("keyword") String keyword,
                                         @Param("limit") int limit,
                                         @Param("offset") int offset);
}
