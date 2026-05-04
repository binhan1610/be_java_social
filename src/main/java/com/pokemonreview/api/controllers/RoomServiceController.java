package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.dto.PagedResponse;
import com.pokemonreview.api.models.RoomService;
import com.pokemonreview.api.repository.RoomServiceRepository;
import com.pokemonreview.api.service.AuthService;
import com.pokemonreview.api.service.IdGeneratorService;
import com.pokemonreview.api.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/room-services")
public class RoomServiceController {

    @Autowired private RoomServiceRepository roomServiceRepository;
    @Autowired private ValidatorService validatorService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthService authService;

    private long getCurrentWorkspaceId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getWorkspaceId(username);
    }
    // Gán 1 dịch vụ vào phòng
    @PostMapping("/assign")
    public ResponseEntity<?> assignService(@RequestBody String json) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("RoomServiceValidator", json);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);

        RoomService rs = objectMapper.readValue(json, RoomService.class);
        rs.setRoomServiceId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.SERVICE));
        rs.setWorkspaceId(getCurrentWorkspaceId());

        return ResponseEntity.ok(roomServiceRepository.save(rs));
    }

    // Lấy danh sách dịch vụ của 1 phòng cụ thể
    @GetMapping("/room/{roomId}")
    public ResponseEntity<PagedResponse<RoomService>> getByRoom(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(PagedResponse.from(roomServiceRepository.findByRoomId(
                roomId,
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 100),
                        Sort.by(Sort.Order.desc("roomServiceId"))
                )
        )));
    }

    @GetMapping("/workspace")
    public ResponseEntity<PagedResponse<RoomService>> getByWorkspace(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws Exception {
        return ResponseEntity.ok(PagedResponse.from(roomServiceRepository.findByWorkspaceId(
                getCurrentWorkspaceId(),
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 100),
                        Sort.by(Sort.Order.desc("roomServiceId"))
                )
        )));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RoomService>> searchRoomServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) throws Exception {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        String resolvedKeyword = keyword != null ? keyword : q;
        return ResponseEntity.ok(roomServiceRepository.searchByKeyword(
                getCurrentWorkspaceId(),
                resolvedKeyword,
                safeLimit,
                safeOffset
        ));
    }

    @DeleteMapping("/room/{roomId}/service/{serviceId}")
    public ResponseEntity<String> remove(@PathVariable long roomId, @PathVariable long serviceId) {
        roomServiceRepository.deleteByRoomIdAndServiceId(roomId, serviceId);
        return ResponseEntity.ok("Đã gỡ dịch vụ khỏi phòng");
    }
}
