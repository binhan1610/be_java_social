package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.pokemonreview.api.dto.PagedResponse;
import com.pokemonreview.api.models.ServiceCatalog;
import com.pokemonreview.api.repository.ServiceCatalogRepository;
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
@RequestMapping("/api/service-catalogs")
public class ServiceCatalogController {

    @Autowired private ServiceCatalogRepository serviceCatalogRepository;
    @Autowired private ValidatorService validatorService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthService authService;

    private long getCurrentWorkspaceId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getWorkspaceId(username);
    }
    @PostMapping
    public ResponseEntity<?> addService(@RequestBody String json) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("AddServiceCatalogValidator", json);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);

        ServiceCatalog service = objectMapper.readValue(json, ServiceCatalog.class);
        service.setServiceId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.SERVICE));
        service.setWorkspaceId(getCurrentWorkspaceId());

        return ResponseEntity.ok(serviceCatalogRepository.save(service));
    }

    @GetMapping("/workspace")
    public ResponseEntity<PagedResponse<ServiceCatalog>> getByWorkspace(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws Exception {
        return ResponseEntity.ok(PagedResponse.from(serviceCatalogRepository.findByWorkspaceId(
                getCurrentWorkspaceId(),
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 100),
                        Sort.by(Sort.Order.desc("serviceId"))
                )
        )));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceCatalog>> searchServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) throws Exception {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        String resolvedKeyword = keyword != null ? keyword : q;
        return ResponseEntity.ok(serviceCatalogRepository.searchByKeyword(
                getCurrentWorkspaceId(),
                resolvedKeyword,
                safeLimit,
                safeOffset
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody String json) throws Exception {
        Set<ValidationMessage> errors = validatorService.validate("AddServiceCatalogValidator", json);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);

        ServiceCatalog updateData = objectMapper.readValue(json, ServiceCatalog.class);
        return serviceCatalogRepository.findById(id).map(s -> {
            s.setName(updateData.getName());
            s.setPrice(updateData.getPrice());
            s.setUnit(updateData.getUnit());
            return ResponseEntity.ok(serviceCatalogRepository.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        serviceCatalogRepository.deleteById(id);
        return ResponseEntity.ok("Xóa thành công dịch vụ: " + id);
    }
}
