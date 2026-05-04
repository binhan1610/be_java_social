package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.MeterReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    // Tìm chỉ số của một phòng trong tháng/năm cụ thể
    Optional<MeterReading> findByRoomIdAndMonthAndYear(long roomId, int month, int year);

    // Lấy lịch sử ghi chỉ số của một phòng
    Page<MeterReading> findByRoomIdOrderByYearDescMonthDesc(long roomId, Pageable pageable);

    // Lấy tất cả chỉ số trong một workspace
    Page<MeterReading> findByWorkspaceId(long workspaceId, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM meterreading
            WHERE workspace_id = :workspaceId
              AND (
                    COALESCE(:keyword, '') = ''
                    OR CAST(meter_reading_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(room_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(month AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(year AS TEXT) ILIKE '%' || :keyword || '%'
                  )
            ORDER BY year DESC, month DESC, meter_reading_id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<MeterReading> searchByKeyword(@Param("workspaceId") long workspaceId,
                                       @Param("keyword") String keyword,
                                       @Param("limit") int limit,
                                       @Param("offset") int offset);
}
