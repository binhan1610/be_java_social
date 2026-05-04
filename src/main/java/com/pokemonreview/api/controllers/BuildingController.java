package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;

import com.pokemonreview.api.dto.AddOrUpdateBuildingDto;
import com.pokemonreview.api.dto.PagedResponse;
import com.pokemonreview.api.models.Building;
import com.pokemonreview.api.repository.BuildingRepository;
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
@RequestMapping("/api/buildings")
public class BuildingController {

    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private AuthService authService;

    // 1. Thêm mới tòa nhà
    @PostMapping
    public ResponseEntity<?> addBuilding(@RequestBody String addJson) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Set<ValidationMessage> errors = validatorService.validate("addOrUpdateValidator", addJson);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        AddOrUpdateBuildingDto dto = objectMapper.readValue(addJson, AddOrUpdateBuildingDto.class);
        Building building = new Building();
        building.setBuildingId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.BUILDING));
        building.setName(dto.getName());
        building.setWorkspaceId(authService.getWorkspaceId(username));
        building.setAddress(dto.getAddress());

        return ResponseEntity.ok(buildingRepository.save(building));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<PagedResponse<Building>> getByWorkspace(
            @PathVariable Long workspaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(PagedResponse.from(buildingRepository.findByWorkspaceId(
                workspaceId,
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 100),
                        Sort.by(Sort.Order.desc("buildingId"))
                )
        )));
    }

    @GetMapping("/workspace")
    public ResponseEntity<PagedResponse<Building>> getByCurrentWorkspace(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws Exception {
        return ResponseEntity.ok(PagedResponse.from(buildingRepository.findByWorkspaceId(
                authService.getWorkspaceId(SecurityContextHolder.getContext().getAuthentication().getName()),
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 100),
                        Sort.by(Sort.Order.desc("buildingId"))
                )
        )));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Building>> searchBuildings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) throws Exception {
        long workspaceId = authService.getWorkspaceId(SecurityContextHolder.getContext().getAuthentication().getName());
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        String resolvedKeyword = keyword != null ? keyword : q;
        return ResponseEntity.ok(buildingRepository.searchByKeyword(workspaceId, resolvedKeyword, safeLimit, safeOffset));
    }

    // 3. Lấy chi tiết 1 tòa nhà
    @GetMapping("/{id}")
    public ResponseEntity<Building> getById(@PathVariable Long id) {
        return buildingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Cập nhật thông tin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBuilding(@PathVariable Long id, @RequestBody String updateJson) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("addOrUpdateValidator", updateJson);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        AddOrUpdateBuildingDto dto = objectMapper.readValue(updateJson, AddOrUpdateBuildingDto.class);
        return buildingRepository.findById(id).map(building -> {
            building.setName(dto.getName());
            building.setAddress(dto.getAddress());
            return ResponseEntity.ok(buildingRepository.save(building));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Xóa tòa nhà
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBuilding(@PathVariable Long id) {
        if (buildingRepository.existsById(id)) {
            buildingRepository.deleteById(id);
            return ResponseEntity.ok("Xóa thành công tòa nhà: " + id);
        }
        return ResponseEntity.notFound().build();
    }
}
