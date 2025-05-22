package com.sazark.kykbecayis.misc.mapper;

import com.sazark.kykbecayis.block.Block;
import com.sazark.kykbecayis.dorm.Dorm;
import com.sazark.kykbecayis.misc.dto.DormDto;
import com.sazark.kykbecayis.misc.Mapper;
import com.sazark.kykbecayis.block.BlockRepository;
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
                .location(dorm.getLocation())
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
                .location(dormDto.getLocation())
                .blocks(dormDto.getBlockIds() != null
                        ? blockRepository.findAllById(dormDto.getBlockIds())
                        : new ArrayList<>())
                .build();
    }
}
