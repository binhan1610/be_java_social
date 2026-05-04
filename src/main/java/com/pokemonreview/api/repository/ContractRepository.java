package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Page<Contract> findByRoomId(long roomId, Pageable pageable);
    Page<Contract> findByTenantId(long tenantId, Pageable pageable);
    Page<Contract> findByWorkspaceId(long workspaceId, Pageable pageable);

    Optional<Contract> findByRoomIdAndStatus(long roomId, String active);

    @Query(value = """
            SELECT *
            FROM contract
            WHERE workspace_id = :workspaceId
              AND (
                    COALESCE(:keyword, '') = ''
                    OR CAST(contract_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(room_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(tenant_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR COALESCE(status, '') ILIKE '%' || :keyword || '%'
                  )
            ORDER BY update_time DESC, contract_id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Contract> searchByKeyword(@Param("workspaceId") long workspaceId,
                                   @Param("keyword") String keyword,
                                   @Param("limit") int limit,
                                   @Param("offset") int offset);
}
