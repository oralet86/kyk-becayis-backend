package com.sazark.kykbecayis.housing.dorm;

import com.sazark.kykbecayis.housing.dto.DormDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
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
     *
     * @param ifModifiedSinceMillis Last modified stamp of the client (in ms)
     */
    @GetMapping
    public ResponseEntity<List<DormDto>> getDorms(
            @RequestHeader(value = "If-Modified-Since", required = false) Long ifModifiedSinceMillis
    ) {
        Instant lastModified = dormService.findLastModifiedTime();
        long lastModifiedMillis = lastModified.toEpochMilli();

        if (ifModifiedSinceMillis != null && lastModifiedMillis >= ifModifiedSinceMillis) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.ok()
                .lastModified(lastModifiedMillis)
                .body(dormService.findAll());
    }
}
