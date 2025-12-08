package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataRepository extends JpaRepository<DataEntity, Long> {

    // 1. Lấy bản ghi mới nhất theo topic
    DataEntity findFirstByTopicOrderByIdDesc(String topic);

    // 2. Lấy danh sách theo limit + offset và topic
    @Query(value = "SELECT * FROM data WHERE topic = ?3 ORDER BY id DESC LIMIT ?1 OFFSET ?2", nativeQuery = true)
    List<DataEntity> getDataWithLimitOffset(int limit, int offset, String topic);

    // 3. Lấy danh sách theo khoảng timestamp và topic
    @Query(value = "SELECT * FROM data WHERE id BETWEEN ?1 AND ?2 AND topic = ?3 ORDER BY id ASC", nativeQuery = true)
    List<DataEntity> findByIdBetweenAndTopicOrderByIdAsc(long from, long to, String topic);
}
