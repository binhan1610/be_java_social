package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Log_History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogHistoryRepository extends JpaRepository<Log_History,Long> {
}
