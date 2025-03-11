package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
