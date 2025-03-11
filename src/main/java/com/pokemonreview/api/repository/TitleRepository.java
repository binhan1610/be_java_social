package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Title;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepository extends JpaRepository<Title, Long> {
}
