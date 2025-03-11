package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Image;
import com.pokemonreview.api.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    Language findById(long id);
}
