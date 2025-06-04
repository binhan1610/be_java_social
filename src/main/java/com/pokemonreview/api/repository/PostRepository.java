package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.PostEntity;
import com.pokemonreview.api.models.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    // JpaRepository đã hỗ trợ sẵn các hàm save, findAll, findById, deleteById

    List<PostEntity> findById(long id);
    PostEntity findByPostId(long id);

    @Query("SELECT p FROM PostEntity p WHERE p.id IN :ids")
    List<PostEntity> getListPostInIds(@Param("ids") List<Long> ids);
}