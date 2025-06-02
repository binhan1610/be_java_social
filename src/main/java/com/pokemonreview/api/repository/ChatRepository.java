package com.pokemonreview.api.repository;


import com.pokemonreview.api.models.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    @Query(value = "SELECT * FROM chat WHERE id = :id ORDER BY create_time DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ChatEntity> findAllByIdWithLimit(@Param("id") Long id,
                                          @Param("limit") int limit,
                                          @Param("offset") int offset);

    ChatEntity findByIdAndCreateTime(long id, long createTime);

}
