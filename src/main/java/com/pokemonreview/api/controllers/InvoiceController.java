package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.models.Invoice;
import com.pokemonreview.api.repository.InvoiceRepository;
import com.pokemonreview.api.service.AuthService;
import com.pokemonreview.api.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private ValidatorService validatorService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthService authService;

    private long getCurrentWorkspaceId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getWorkspaceId(username);
    }

    // 1. LẤY DANH SÁCH HÓA ĐƠN THEO WORKSPACE (Dùng cho trang quản lý tổng)
    @GetMapping("/workspace")
    public ResponseEntity<List<Invoice>> getAllInvoices() throws Exception {
        return ResponseEntity.ok(invoiceRepository.findByWorkspaceId(getCurrentWorkspaceId()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Invoice>> searchInvoices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) throws Exception {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        String resolvedKeyword = keyword != null ? keyword : q;
        return ResponseEntity.ok(invoiceRepository.searchByKeyword(
                getCurrentWorkspaceId(),
                resolvedKeyword,
                safeLimit,
                safeOffset
        ));
    }

    // 2. LẤY HÓA ĐƠN CỦA 1 PHÒNG CỤ THỂ
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Invoice>> getByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(invoiceRepository.findByRoomIdOrderByYearDescMonthDesc(roomId));
    }

    // 3. CHI TIẾT 1 HÓA ĐƠN
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. THANH TOÁN HÓA ĐƠN (Hàm quan trọng nhất)
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payInvoice(@PathVariable Long id, @RequestParam long amount) {
        return invoiceRepository.findById(id).map(invoice -> {
            long newPaidAmount = invoice.getPaidAmount() + amount;
            invoice.setPaidAmount(newPaidAmount);

            // Nếu khách đóng đủ hoặc thừa thì chuyển trạng thái PAID
            if (newPaidAmount >= invoice.getTotalAmount()) {
                invoice.setStatus("PAID");
                invoice.setPaymentTime(System.currentTimeMillis());
            } else {
                invoice.setStatus("PARTIAL"); // Thanh toán một phần
            }

            invoice.setUpdateTime(System.currentTimeMillis());
            return ResponseEntity.ok(invoiceRepository.save(invoice));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. CẬP NHẬT THỦ CÔNG (Nếu cần sửa tổng tiền)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInvoice(@PathVariable Long id, @RequestBody String json) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("UpdateInvoiceValidator", json);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);

        Invoice updateData = objectMapper.readValue(json, Invoice.class);
        return invoiceRepository.findById(id).map(invoice -> {
            invoice.setTotalAmount(updateData.getTotalAmount());
            invoice.setStatus(updateData.getStatus());
            invoice.setUpdateTime(System.currentTimeMillis());
            return ResponseEntity.ok(invoiceRepository.save(invoice));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 6. XÓA HÓA ĐƠN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        invoiceRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa hóa đơn ID: " + id);
    }
}
