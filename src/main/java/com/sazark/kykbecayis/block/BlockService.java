package com.sazark.kykbecayis.block;

import com.sazark.kykbecayis.misc.dto.BlockDto;
import com.sazark.kykbecayis.misc.mapper.BlockMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        Block block = blockRepository.findById(id).orElse(null);
        return blockMapper.toDTO(block);
    }

    public List<BlockDto> findAll() {
        return blockRepository.findAll()
                .stream()
                .map(blockMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BlockDto> findByDormId(Long dormId) {
        return blockRepository.findByDormId(dormId)
                .stream()
                .map(blockMapper::toDTO)
                .collect(Collectors.toList());
    }



    public boolean delete(Long id) {
        if (!blockRepository.existsById(id)) {
            return false;
        }
        blockRepository.deleteById(id);
        return true;
    }
}
