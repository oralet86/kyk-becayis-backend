package com.sazark.kykbecayis.housing.block;

import com.sazark.kykbecayis.housing.dto.BlockDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blocks")
public class BlockController {
    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @GetMapping
    public ResponseEntity<?> getBlocks(
            @RequestParam(required = false) Long blockId,
            @RequestParam(required = false) Long dormId
    ) {
        if (blockId != null) {
            BlockDto block = blockService.findById(blockId);
            return (block != null) ? ResponseEntity.ok(block) : ResponseEntity.notFound().build();
        }

        if (dormId != null) {
            return ResponseEntity.ok(blockService.findByDormId(dormId));
        }

        return ResponseEntity.ok(blockService.findAll());
    }
}