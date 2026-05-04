package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.dto.AddTenantWithContractDto;
import com.pokemonreview.api.dto.PagedResponse;
import com.pokemonreview.api.models.Contract;
import com.pokemonreview.api.models.Tenant;
import com.pokemonreview.api.repository.ContractRepository;
import com.pokemonreview.api.repository.TenantRepository;
import com.pokemonreview.api.service.AuthService;
import com.pokemonreview.api.service.IdGeneratorService;
import com.pokemonreview.api.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired private TenantRepository tenantRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private ValidatorService validatorService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthService authService;

    private long getCurrentWorkspaceId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getWorkspaceId(username);
    }

    // --- 1. THÊM KHÁCH & TẠO HỢP ĐỒNG ---
    @PostMapping
    @Transactional
    public ResponseEntity<?> addTenant(@RequestBody String json) throws Exception {
        // Validate JSON Schema (File: AddTenantValidator.json)
        Set<ValidationMessage> errors = validatorService.validate("AddTenantValidator", json);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        AddTenantWithContractDto dto = objectMapper.readValue(json, AddTenantWithContractDto.class);
        long now = System.currentTimeMillis();
        long wsId = getCurrentWorkspaceId();

        // 1.1 Khởi tạo Tenant
        Tenant tenant = tenantRepository.findTenantByIdentityCard(dto.getIdentityCard());
        if(tenant == null){
            tenant = new Tenant();
            long tId = IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.TENANT);
            tenant.setTenantId(tId);
            tenant.setWorkspaceId(wsId);
            tenant.setFullName(dto.getFullName());
            tenant.setPhone(dto.getPhone());
            tenant.setIdentityCard(dto.getIdentityCard());
            tenant.setImageUrl(dto.getImageUrl());
            tenant.setCreateTime(now);
            tenant.setUpdateTime(now);
            tenantRepository.save(tenant);
        }

        // 1.2 Khởi tạo Contract
        Contract contract = new Contract();
        contract.setContractId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.CONTRACT));
        contract.setWorkspaceId(wsId);
        contract.setRoomId(dto.getRoomId());
        contract.setTenantId(tenant.getTenantId()); // Link bằng ID phẳng

        // Logic tính toán thời gian
        long startDate = now;
        long endDate = calculateEndDate(startDate, dto.getDuration());

        contract.setStartDate(startDate);
        contract.setEndDate(endDate);
        contract.setDepositAmount(dto.getDepositAmount());
        contract.setStatus(dto.getContractStatus() != null ? dto.getContractStatus() : "ACTIVE");
        contract.setCreateTime(now);
        contract.setUpdateTime(now);
        contractRepository.save(contract);

        return ResponseEntity.ok("Thêm khách và tạo hợp đồng thành công. ID Khách: " + tenant.getTenantId());
    }

    // --- 2. CẬP NHẬT THÔNG TIN KHÁCH (Update riêng) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenant(@PathVariable Long id, @RequestBody String json) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("UpdateTenantValidator", json);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);

        Tenant updateData = objectMapper.readValue(json, Tenant.class);
        return tenantRepository.findById(id).map(tenant -> {
            tenant.setFullName(updateData.getFullName());
            tenant.setPhone(updateData.getPhone());
            tenant.setIdentityCard(updateData.getIdentityCard());
            tenant.setImageUrl(updateData.getImageUrl());
            tenant.setUpdateTime(System.currentTimeMillis());
            return ResponseEntity.ok(tenantRepository.save(tenant));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- 3. LẤY CHI TIẾT KHÁCH ---
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        return tenantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public ResponseEntity<PagedResponse<Tenant>> getByWorkspace(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws Exception {
        return ResponseEntity.ok(PagedResponse.from(tenantRepository.findByWorkspaceId(
                getCurrentWorkspaceId(),
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 100),
                        Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("tenantId"))
                )
        )));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Tenant>> searchTenants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) throws Exception {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        String resolvedKeyword = keyword != null ? keyword : q;
        return ResponseEntity.ok(tenantRepository.searchByKeyword(
                getCurrentWorkspaceId(),
                resolvedKeyword,
                safeLimit,
                safeOffset
        ));
    }

    // --- 4. XÓA KHÁCH ---
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteTenant(@PathVariable Long id) {
        if (tenantRepository.existsById(id)) {
            tenantRepository.deleteById(id);
            // Bạn nên cân nhắc xóa hoặc đóng hợp đồng của khách này ở đây
            return ResponseEntity.ok("Đã xóa khách ID: " + id);
        }
        return ResponseEntity.notFound().build();
    }

    // --- HELPER: TÍNH END DATE ---
    private long calculateEndDate(long startMs, String duration) {
        if (duration == null || !duration.endsWith("M")) return startMs;

        int months = Integer.parseInt(duration.replace("M", ""));
        return Instant.ofEpochMilli(startMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusMonths(months)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}
