package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Tenant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    // Thêm hàm này để sau này bạn lọc khách theo từng khu vực làm việc
    List<Tenant> findByWorkspaceId(long workspaceId);

    Tenant findTenantByIdentityCard(String identityCard);

    @Query(value = """
            SELECT *
            FROM tenant
            WHERE workspace_id = :workspaceId
              AND (
                    COALESCE(:keyword, '') = ''
                    OR CAST(tenant_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR COALESCE(full_name, '') ILIKE '%' || :keyword || '%'
                    OR COALESCE(phone, '') ILIKE '%' || :keyword || '%'
                    OR COALESCE(identity_card, '') ILIKE '%' || :keyword || '%'
                  )
            ORDER BY create_time DESC, tenant_id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Tenant> searchByKeyword(@Param("workspaceId") long workspaceId,
                                 @Param("keyword") String keyword,
                                 @Param("limit") int limit,
                                 @Param("offset") int offset);
}
