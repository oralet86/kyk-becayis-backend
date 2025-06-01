package com.sazark.kykbecayis.posting;

import com.sazark.kykbecayis.misc.dto.PostingDto;
import com.sazark.kykbecayis.misc.request.PostingCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/postings")
public class PostingController {
    private final PostingService postingService;

    public PostingController(PostingService postingService) {
        this.postingService = postingService;
    }

    @PostMapping
    public ResponseEntity<PostingDto> createPosting(@RequestBody PostingCreateRequest postingCreateRequest) {
        PostingDto savedPosting = postingService.create(postingCreateRequest);
        return ResponseEntity
                .created(URI.create("/api/postings/" + savedPosting.getId()))
                .body(savedPosting);
    }

    @GetMapping
    public ResponseEntity<?> getPostings(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long sourceDormId,
            @RequestParam(required = false) Long targetDormId
    ) {
        if (id != null) {
            PostingDto posting = postingService.findById(id);
            return (posting != null) ? ResponseEntity.ok(posting) : ResponseEntity.notFound().build();
        }

        if (userId != null || sourceDormId != null || targetDormId != null) {
            return ResponseEntity.ok(postingService.filterPostings(userId, sourceDormId, targetDormId));
        }

        return ResponseEntity.ok(postingService.findAll());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getPostingCount(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long sourceDormId,
            @RequestParam(required = false) Long targetDormId
    ) {
        long count = postingService.countPostings(userId, sourceDormId, targetDormId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostingDto> updatePosting(@PathVariable Long id, @RequestBody PostingDto postingDto) {
        PostingDto updatedPosting = postingService.update(id, postingDto);
        return (updatedPosting != null) ? ResponseEntity.ok(updatedPosting) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosting(@PathVariable Long id) {
        boolean deleted = postingService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}