package com.sazark.kykbecayis.controllers;

import com.sazark.kykbecayis.domain.dto.DormDto;
import com.sazark.kykbecayis.services.DormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/dorms")
public class DormController {
    private final DormService dormService;

    public DormController(DormService dormService) {
        this.dormService = dormService;
    }

    @PostMapping
    public ResponseEntity<DormDto> createDorm(@RequestBody DormDto dormDto) {
        DormDto savedDorm = dormService.create(dormDto);
        return ResponseEntity
                .created(URI.create("/api/postings/" + savedDorm.getId()))
                .body(savedDorm);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DormDto> getDormById(@PathVariable Long id) {
        DormDto dormDto = dormService.findById(id);
        return (dormDto != null) ? ResponseEntity.ok(dormDto) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<DormDto>> getAllDorms() {
        return ResponseEntity.ok(dormService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DormDto> updateDorm(@PathVariable Long id, @RequestBody DormDto dormDto) {
        DormDto updatedDorm = dormService.update(id, dormDto);
        return (updatedDorm != null) ? ResponseEntity.ok(updatedDorm) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDorm(@PathVariable Long id) {
        boolean deleted = dormService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
