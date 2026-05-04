package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Lấy danh sách hóa đơn của một phòng
    Page<Invoice> findByRoomIdOrderByYearDescMonthDesc(long roomId, Pageable pageable);

    // Tìm hóa đơn cụ thể của phòng theo tháng/năm
    Optional<Invoice> findByRoomIdAndMonthAndYear(long roomId, int month, int year);

    // Lấy tất cả hóa đơn chưa thanh toán trong một workspace
    List<Invoice> findByWorkspaceIdAndStatus(long workspaceId, String status);

    // Lấy toàn bộ hóa đơn của một workspace
    Page<Invoice> findByWorkspaceId(long workspaceId, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM invoice
            WHERE workspace_id = :workspaceId
              AND (
                    COALESCE(:keyword, '') = ''
                    OR CAST(invoice_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(room_id AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(month AS TEXT) ILIKE '%' || :keyword || '%'
                    OR CAST(year AS TEXT) ILIKE '%' || :keyword || '%'
                    OR COALESCE(status, '') ILIKE '%' || :keyword || '%'
                  )
            ORDER BY year DESC, month DESC, invoice_id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Invoice> searchByKeyword(@Param("workspaceId") long workspaceId,
                                  @Param("keyword") String keyword,
                                  @Param("limit") int limit,
                                  @Param("offset") int offset);
}
