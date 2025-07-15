package com.sazark.kykbecayis.housing.dorm;

import com.sazark.kykbecayis.housing.dto.DormDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dorms")
public class DormController {
    private final DormService dormService;

    public DormController(DormService dormService) {
        this.dormService = dormService;
    }

    @GetMapping
    public ResponseEntity<?> getDorms(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name
    ) {
        if (id != null) {
            DormDto dorm = dormService.findById(id);
            return (dorm != null) ? ResponseEntity.ok(dorm) : ResponseEntity.notFound().build();
        }

        boolean hasFilters = type != null || city != null || name != null;
        if (hasFilters) {
            return ResponseEntity.ok(dormService.filterDorms(type, city, name));
        }

        return ResponseEntity.ok(dormService.findAll());
    }

}
