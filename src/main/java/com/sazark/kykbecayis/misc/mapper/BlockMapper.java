package com.sazark.kykbecayis.misc.mapper;

import com.sazark.kykbecayis.block.Block;
import com.sazark.kykbecayis.misc.dto.BlockDto;
import com.sazark.kykbecayis.misc.Mapper;
import com.sazark.kykbecayis.dorm.DormRepository;
import org.springframework.stereotype.Component;

@Component
public class BlockMapper implements Mapper<Block, BlockDto> {

    private final DormRepository dormRepository;

    public BlockMapper(DormRepository dormRepository) {
        this.dormRepository = dormRepository;
    }

    @Override
    public BlockDto toDTO(Block block) {
        if (block == null) return null;

        return BlockDto.builder()
                .id(block.getId())
                .type(block.getType())
                .fullAddress(block.getFullAddress())
                .city(block.getCity())
                .location(block.getLocation())
                .name(block.getName())
                .dormId(block.getDorm().getId())
                .build();
    }

    @Override
    public Block toEntity(BlockDto blockDto) {
        if (blockDto == null) return null;

        return Block.builder()
                .id(blockDto.getId())
                .type(blockDto.getType())
                .fullAddress(blockDto.getFullAddress())
                .city(blockDto.getCity())
                .location(blockDto.getLocation())
                .name(blockDto.getName())
                .dorm(blockDto.getDormId() != null
                        ? dormRepository.findById(blockDto.getDormId()).orElse(null)
                        : null)
                .build();
    }
}
