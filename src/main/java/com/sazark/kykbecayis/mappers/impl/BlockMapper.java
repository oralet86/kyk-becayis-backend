package com.sazark.kykbecayis.mappers.impl;

import com.sazark.kykbecayis.domain.dto.BlockDto;
import com.sazark.kykbecayis.domain.entities.Block;
import com.sazark.kykbecayis.mappers.Mapper;
import com.sazark.kykbecayis.repositories.DormRepository;
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
                .province(block.getProvince())
                .latitude(block.getLatitude())
                .longitude(block.getLongitude())
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
                .province(blockDto.getProvince())
                .latitude(blockDto.getLatitude())
                .longitude(blockDto.getLongitude())
                .dorm(blockDto.getDormId() != null
                        ? dormRepository.findById(blockDto.getDormId()).orElse(null)
                        : null)
                .build();
    }
}
