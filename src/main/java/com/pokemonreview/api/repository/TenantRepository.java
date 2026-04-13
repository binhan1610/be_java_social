package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    // Thêm hàm này để sau này bạn lọc khách theo từng khu vực làm việc
    List<Tenant> findByWorkspaceId(long workspaceId);

    Tenant findTenantByIdentityCard(String identityCard);
}