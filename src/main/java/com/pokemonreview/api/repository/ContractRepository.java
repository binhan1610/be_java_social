package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByRoomId(long roomId);
    List<Contract> findByTenantId(long tenantId);
    List<Contract> findByWorkspaceId(long workspaceId);
}