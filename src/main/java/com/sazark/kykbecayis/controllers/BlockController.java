package com.sazark.kykbecayis.controllers;

import com.sazark.kykbecayis.domain.dto.BlockDto;
import com.sazark.kykbecayis.services.BlockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/blocks")
public class BlockController {
    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @PostMapping
    public ResponseEntity<BlockDto> createBlock(@RequestBody BlockDto blockDto) {
        BlockDto savedBlock = blockService.create(blockDto);
        return ResponseEntity
                .created(URI.create("/api/blocks/" + savedBlock.getId()))
                .body(savedBlock);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlockDto> getBlock(@PathVariable Long id) {
        BlockDto block = blockService.findById(id);
        return (block != null) ? ResponseEntity.ok(block) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<BlockDto>> getAllBlocks() {
        return ResponseEntity.ok(blockService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlockDto> updateBlock(@PathVariable Long id, @RequestBody BlockDto blockDto) {
        BlockDto updated = blockService.update(id, blockDto);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable Long id) {
        boolean deleted = blockService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}