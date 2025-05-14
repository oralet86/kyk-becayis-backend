package com.sazark.kykbecayis.mappers.impl;

import com.sazark.kykbecayis.domain.dto.DormDto;
import com.sazark.kykbecayis.domain.entities.Block;
import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.mappers.Mapper;
import com.sazark.kykbecayis.repositories.BlockRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DormMapper implements Mapper<Dorm, DormDto> {

    private final BlockRepository blockRepository;

    public DormMapper(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    @Override
    public DormDto toDTO(Dorm dorm) {
        if (dorm == null) return null;

        return DormDto.builder()
                .id(dorm.getId())
                .type(dorm.getType())
                .fullAddress(String.valueOf(dorm.getFullAddress()))
                .city(dorm.getCity())
                .name(dorm.getName())
                .phoneNumber(String.valueOf(dorm.getPhoneNumber()))
                .blockIds(dorm.getBlocks() != null
                        ? dorm.getBlocks().stream().map(Block::getId).toList()
                        : new ArrayList<>())
                .build();
    }

    @Override
    public Dorm toEntity(DormDto dormDto) {
        if (dormDto == null) return null;

        return Dorm.builder()
                .id(dormDto.getId())
                .type(dormDto.getType())
                .fullAddress(dormDto.getFullAddress())
                .city(dormDto.getCity())
                .name(dormDto.getName())
                .phoneNumber(dormDto.getPhoneNumber())
                .blocks(dormDto.getBlockIds() != null
                        ? blockRepository.findAllById(dormDto.getBlockIds())
                        : new ArrayList<>())
                .build();
    }
}
