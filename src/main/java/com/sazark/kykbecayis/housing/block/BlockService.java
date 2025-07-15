package com.sazark.kykbecayis.housing.block;

import com.sazark.kykbecayis.core.mapper.BlockMapper;
import com.sazark.kykbecayis.housing.dto.BlockDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockService {
    private final BlockRepository blockRepository;
    private final BlockMapper blockMapper;

    public BlockService(BlockRepository blockRepository, BlockMapper blockMapper) {
        this.blockRepository = blockRepository;
        this.blockMapper = blockMapper;
    }

    public BlockDto create(BlockDto blockDto) {
        Block block = blockMapper.toEntity(blockDto);
        Block savedBlock = blockRepository.save(block);
        return blockMapper.toDTO(savedBlock);
    }

    public BlockDto update(Long id, BlockDto blockDto) {
        if (!blockRepository.existsById(id)) {
            return null;
        }

        Block block = blockMapper.toEntity(blockDto);
        block.setId(id);
        Block updatedBlock = blockRepository.save(block);
        return blockMapper.toDTO(updatedBlock);
    }

    public BlockDto findById(Long id) {
        return blockRepository.findById(id)
                .map(blockMapper::toDTO)
                .orElse(null);
    }

    public List<BlockDto> findAll() {
        return blockRepository.findAll().stream()
                .map(blockMapper::toDTO)
                .toList();
    }

    public List<BlockDto> findByDormId(Long dormId) {
        return blockRepository.findAllByDormId(dormId).stream()
                .map(blockMapper::toDTO)
                .toList();
    }

    public boolean delete(Long id) {
        if (!blockRepository.existsById(id)) {
            return false;
        }
        blockRepository.deleteById(id);
        return true;
    }
}
