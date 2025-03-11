package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Lable;
import com.pokemonreview.api.models.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LableRepository extends JpaRepository<Lable,Long> {
    Page<Lable> findLableByUser_Username(String username, Pageable pageable);

//    @Query("DELETE from Lable where id = :lable_id"
//    )
//    void deleteLable(int lable_id);
}
