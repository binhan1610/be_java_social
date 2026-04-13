package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByWorkspaceId(long workspaceId);
}