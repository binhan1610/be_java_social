package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.models.MeterReading;
import com.pokemonreview.api.repository.MeterReadingRepository;
import com.pokemonreview.api.service.AuthService;
import com.pokemonreview.api.service.IdGeneratorService;
import com.pokemonreview.api.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/meter-readings")
public class MeterReadingController {

    @Autowired private MeterReadingRepository meterReadingRepository;
    @Autowired private ValidatorService validatorService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthService authService;

    private long getCurrentWorkspaceId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getWorkspaceId(username);
    }

    // 1. Ghi chỉ số mới
    @PostMapping
    public ResponseEntity<?> addReading(@RequestBody String json) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("MeterReadingValidator", json);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        MeterReading reading = objectMapper.readValue(json, MeterReading.class);

        // Kiểm tra xem tháng này đã ghi chưa để tránh duplicate
        if (meterReadingRepository.findByRoomIdAndMonthAndYear(
                reading.getRoomId(), reading.getMonth(), reading.getYear()).isPresent()) {
            return ResponseEntity.badRequest().body("Phòng này đã ghi chỉ số cho tháng " + reading.getMonth() + " rồi.");
        }

        reading.setMeterReadingId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.METER_READING));
        reading.setWorkspaceId(getCurrentWorkspaceId());
        reading.setCreateTime(System.currentTimeMillis());

        return ResponseEntity.ok(meterReadingRepository.save(reading));
    }

    // 2. Lấy lịch sử ghi số của 1 phòng
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MeterReading>> getHistory(@PathVariable Long roomId) {
        return ResponseEntity.ok(meterReadingRepository.findByRoomIdOrderByYearDescMonthDesc(roomId));
    }

    @GetMapping("/workspace")
    public ResponseEntity<List<MeterReading>> getByWorkspace() throws Exception {
        return ResponseEntity.ok(meterReadingRepository.findByWorkspaceId(getCurrentWorkspaceId()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MeterReading>> searchReadings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) throws Exception {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        String resolvedKeyword = keyword != null ? keyword : q;
        return ResponseEntity.ok(meterReadingRepository.searchByKeyword(
                getCurrentWorkspaceId(),
                resolvedKeyword,
                safeLimit,
                safeOffset
        ));
    }

    // 3. Cập nhật chỉ số (Sửa nếu ghi nhầm)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReading(@PathVariable Long id, @RequestBody String json) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("MeterReadingValidator", json);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);

        MeterReading updateData = objectMapper.readValue(json, MeterReading.class);
        return meterReadingRepository.findById(id).map(r -> {
            r.setElectricOld(updateData.getElectricOld());
            r.setElectricNew(updateData.getElectricNew());
            r.setWaterOld(updateData.getWaterOld());
            r.setWaterNew(updateData.getWaterNew());
            return ResponseEntity.ok(meterReadingRepository.save(r));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Xóa chỉ số
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        meterReadingRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa chỉ số ID: " + id);
    }
}
