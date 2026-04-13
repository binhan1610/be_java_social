package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;

import com.pokemonreview.api.dto.AddOrUpdateBuildingDto;
import com.pokemonreview.api.models.Building;
import com.pokemonreview.api.repository.BuildingRepository;
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
@RequestMapping("/api/buildings")
public class BuildingController {

    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private AuthService authService;

    // 1. Thêm mới tòa nhà
    @PostMapping("/add")
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
    public ResponseEntity<List<Building>> getByWorkspace(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(buildingRepository.findByWorkspaceId(workspaceId));
    }

    // 3. Lấy chi tiết 1 tòa nhà
    @GetMapping("/{id}")
    public ResponseEntity<Building> getById(@PathVariable Long id) {
        return buildingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Cập nhật thông tin
    @PutMapping("/update/{id}")
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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBuilding(@PathVariable Long id) {
        if (buildingRepository.existsById(id)) {
            buildingRepository.deleteById(id);
            return ResponseEntity.ok("Xóa thành công tòa nhà: " + id);
        }
        return ResponseEntity.notFound().build();
    }
}