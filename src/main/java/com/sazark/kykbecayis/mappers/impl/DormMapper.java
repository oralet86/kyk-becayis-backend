package com.sazark.kykbecayis.mappers.impl;

import com.sazark.kykbecayis.domain.dto.DormDto;
import com.sazark.kykbecayis.domain.entities.Block;
import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.mappers.Mapper;
import com.sazark.kykbecayis.repositories.BlockRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
                .province(dorm.getProvince())
                .latitude(dorm.getLatitude())
                .longitude(dorm.getLongitude())
                .name(dorm.getName())
                .phoneNumber(String.valueOf(dorm.getPhoneNumber()))
                .faxNumber(String.valueOf(dorm.getFaxNumber()))
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
                .province(dormDto.getProvince())
                .latitude(dormDto.getLatitude())
                .longitude(dormDto.getLongitude())
                .name(dormDto.getName())
                .phoneNumber(dormDto.getPhoneNumber())
                .faxNumber(dormDto.getFaxNumber())
                .blocks(dormDto.getBlockIds() != null
                        ? blockRepository.findAllById(dormDto.getBlockIds())
                        : new ArrayList<>())
                .build();
    }
}
