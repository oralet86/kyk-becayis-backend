package com.sazark.kykbecayis.posting;

import com.sazark.kykbecayis.misc.dto.PostingDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/postings")
public class PostingController {
    private final PostingService postingService;

    public PostingController(PostingService postingService) {
        this.postingService = postingService;
    }

    @PostMapping
    public ResponseEntity<PostingDto> createPosting(@RequestBody PostingDto postingDto) {
        PostingDto savedPosting = postingService.create(postingDto);
        return ResponseEntity
                .created(URI.create("/api/postings/" + savedPosting.getId()))
                .body(savedPosting);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostingDto> getPosting(@PathVariable Long id) {
        PostingDto postingDto = postingService.findById(id);
        return (postingDto != null) ? ResponseEntity.ok(postingDto) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<PostingDto>> getAllPostings() {
        return ResponseEntity.ok(postingService.findAll());
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