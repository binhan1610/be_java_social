package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.dto.AddOrUpdateRoomDto;
import com.pokemonreview.api.dto.PagedResponse;
import com.pokemonreview.api.models.Room;
import com.pokemonreview.api.repository.RoomRepository;
import com.pokemonreview.api.service.AuthService;
import com.pokemonreview.api.service.IdGeneratorService;
import com.pokemonreview.api.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private AuthService authService;

    private long getCurrentWorkspaceId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getWorkspaceId(username);
    }

    // 1. Thêm phòng mới
    @PostMapping
    public ResponseEntity<?> addRoom(@RequestBody String addJson) throws Exception {
        // Validate bằng file AddOrUpdateRoomValidator.json
        Set<ValidationMessage> errors = validatorService.validate("AddOrUpdateRoomValidator", addJson);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        AddOrUpdateRoomDto dto = objectMapper.readValue(addJson, AddOrUpdateRoomDto.class);

        long now = System.currentTimeMillis();
        Room room = new Room();
        room.setRoomId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.ROOM));
        room.setWorkspaceId(getCurrentWorkspaceId());
        room.setBuildingId(dto.getBuildingId());
        room.setName(dto.getName());
        room.setPrice(dto.getPrice());
        room.setStatus(dto.getStatus() != null ? dto.getStatus() : "EMPTY");
        room.setMaxCapacity(dto.getMaxCapacity());
        room.setCreateTime(now);
        room.setUpdateTime(now);

        return ResponseEntity.ok(roomRepository.save(room));
    }

    // 2. Lấy danh sách phòng theo tòa nhà và filter bổ sung
    @GetMapping("/building/{buildingId}")
    public ResponseEntity<PagedResponse<Room>> getByBuilding(
            @PathVariable Long buildingId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws Exception {
        return ResponseEntity.ok(queryRooms(buildingId, status, q, available, page, size));
    }

    // 3. Lấy danh sách phòng theo workspace với filter
    @GetMapping
    public ResponseEntity<PagedResponse<Room>> getRooms(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws Exception {
        return ResponseEntity.ok(queryRooms(buildingId, status, q, available, page, size));
    }

    // 4. Lấy chi tiết 1 phòng
    @GetMapping("/{id}")
    public ResponseEntity<Room> getById(@PathVariable Long id) {
        return roomRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private PagedResponse<Room> queryRooms(
            Long buildingId,
            String status,
            String q,
            Boolean available,
            int page,
            int size
    ) throws Exception {
        return PagedResponse.from(roomRepository.searchRooms(
                getCurrentWorkspaceId(),
                buildingId,
                status,
                q,
                available,
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100))
        ));
    }

    // 4. Cập nhật thông tin phòng
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody String updateJson) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("AddOrUpdateRoomValidator", updateJson);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        AddOrUpdateRoomDto dto = objectMapper.readValue(updateJson, AddOrUpdateRoomDto.class);

        return roomRepository.findById(id).map(room -> {
            room.setName(dto.getName());
            room.setPrice(dto.getPrice());
            room.setStatus(dto.getStatus());
            room.setMaxCapacity(dto.getMaxCapacity());
            room.setUpdateTime(System.currentTimeMillis());
            return ResponseEntity.ok(roomRepository.save(room));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Xóa phòng
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return ResponseEntity.ok("Xóa thành công phòng: " + id);
        }
        return ResponseEntity.notFound().build();
    }
}
