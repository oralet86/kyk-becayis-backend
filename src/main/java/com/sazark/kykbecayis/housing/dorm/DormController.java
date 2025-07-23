package com.sazark.kykbecayis.housing.dorm;

import com.sazark.kykbecayis.housing.dto.DormDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@RequestMapping("/api/dorms")
public class DormController {
    private final DormService dormService;

    public DormController(DormService dormService) {
        this.dormService = dormService;
    }

    /**
     * Returns all dorm and block data. If the client already has the most recent data returns a 304 Not Modified.
     */
    @GetMapping
    public ResponseEntity<List<DormDto>> getAllDorms(WebRequest request) {
        long lastModMillis = dormService.findLastModifiedTime().toEpochMilli();

        // returns true if the client is up-to-date and sends a 304 Not Modified response.
        if (request.checkNotModified(lastModMillis)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        // Otherwise fall through and return data + Last-Modified header
        List<DormDto> dorms = dormService.findAll();
        return ResponseEntity.ok()
                .lastModified(lastModMillis)
                .body(dorms);
    }
}
