package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.models.Contract;
import com.pokemonreview.api.repository.ContractRepository;
import com.pokemonreview.api.service.AuthService;
import com.pokemonreview.api.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired private ContractRepository contractRepository;
    @Autowired private ValidatorService validatorService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired
    private AuthService authService;

    private long getCurrentWorkspaceId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getWorkspaceId(username);
    }

    // --- 1. LẤY CHI TIẾT HỢP ĐỒNG ---
    @GetMapping("/{id}")
    public ResponseEntity<Contract> getById(@PathVariable Long id) {
        return contractRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 2. LẤY DANH SÁCH HỢP ĐỒNG THEO PHÒNG (Lịch sử thuê) ---
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Contract>> getByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(contractRepository.findByRoomId(roomId));
    }

    // --- 3. LẤY DANH SÁCH HỢP ĐỒNG THEO KHÁCH ---
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Contract>> getByTenant(@PathVariable Long tenantId) {
        return ResponseEntity.ok(contractRepository.findByTenantId(tenantId));
    }

    @GetMapping("/workspace")
    public ResponseEntity<List<Contract>> getByCurrentWorkspace() throws Exception {
        return ResponseEntity.ok(contractRepository.findByWorkspaceId(getCurrentWorkspaceId()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Contract>> searchContracts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) throws Exception {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        String resolvedKeyword = keyword != null ? keyword : q;
        return ResponseEntity.ok(contractRepository.searchByKeyword(
                getCurrentWorkspaceId(),
                resolvedKeyword,
                safeLimit,
                safeOffset
        ));
    }

    // --- 4. CẬP NHẬT HỢP ĐỒNG (Trạng thái, tiền cọc...) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContract(@PathVariable Long id, @RequestBody String json) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("UpdateContractValidator", json);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);

        Contract updateData = objectMapper.readValue(json, Contract.class);
        return contractRepository.findById(id).map(contract -> {
            contract.setStatus(updateData.getStatus());
            contract.setDepositAmount(updateData.getDepositAmount());
            contract.setStartDate(updateData.getStartDate());
            contract.setEndDate(updateData.getEndDate());
            contract.setUpdateTime(System.currentTimeMillis());
            return ResponseEntity.ok(contractRepository.save(contract));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- 5. GIA HẠN HỢP ĐỒNG (Extend duration: 6M, 12M...) ---
    // API này nhận vào chuỗi duration để tự tính endDate mới dựa trên endDate cũ
    @PutMapping("/{id}/extend")
    public ResponseEntity<?> extendContract(@PathVariable Long id, @RequestParam String duration) {
        return contractRepository.findById(id).map(contract -> {
            long newEndDate = calculateExtendedEndDate(contract.getEndDate(), duration);
            contract.setEndDate(newEndDate);
            contract.setUpdateTime(System.currentTimeMillis());
            return ResponseEntity.ok(contractRepository.save(contract));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- 6. XÓA HỢP ĐỒNG (Thanh lý) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContract(@PathVariable Long id) {
        if (contractRepository.existsById(id)) {
            contractRepository.deleteById(id);
            return ResponseEntity.ok("Đã xóa/thanh lý hợp đồng ID: " + id);
        }
        return ResponseEntity.notFound().build();
    }

    // HELPER: Tính toán ngày kết thúc mới khi gia hạn
    private long calculateExtendedEndDate(long currentEndDateMs, String duration) {
        if (!duration.endsWith("M")) return currentEndDateMs;
        int months = Integer.parseInt(duration.replace("M", ""));

        return Instant.ofEpochMilli(currentEndDateMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusMonths(months)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}
