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
    @Query("SELECT p FROM PostEntity p WHERE p.id = :id ORDER BY p.createTime DESC")
    List<PostEntity> findById(@Param("id") long id);

    PostEntity findByPostId(long id);

    @Query("SELECT p FROM PostEntity p WHERE p.id IN :ids order by p.createTime desc")
    List<PostEntity> getListPostInIds(@Param("ids") List<Long> ids);
}