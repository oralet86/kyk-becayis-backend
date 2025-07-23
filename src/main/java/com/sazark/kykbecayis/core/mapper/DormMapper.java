package com.sazark.kykbecayis.core.mapper;

import com.sazark.kykbecayis.core.Mapper;
import com.sazark.kykbecayis.housing.block.Block;
import com.sazark.kykbecayis.housing.block.BlockRepository;
import com.sazark.kykbecayis.housing.dorm.Dorm;
import com.sazark.kykbecayis.housing.dto.DormDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DormMapper implements Mapper<Dorm, DormDto> {

    private final BlockRepository blockRepository;
    private final BlockMapper blockMapper;

    public DormMapper(BlockRepository blockRepository, BlockMapper blockMapper) {
        this.blockRepository = blockRepository;
        this.blockMapper = blockMapper;
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
                .blocks(
                        dorm.getBlocks().stream()
                                .map(blockMapper::toDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public Dorm toEntity(DormDto dormDto) {
        if (dormDto == null) return null;

        // 1) Build the Dorm shell
        Dorm dorm = Dorm.builder()
                .id(dormDto.getId())
                .type(dormDto.getType())
                .fullAddress(dormDto.getFullAddress())
                .city(dormDto.getCity())
                .name(dormDto.getName())
                .phoneNumber(dormDto.getPhoneNumber())
                .location(dormDto.getLocation())
                .build();

        // 2) Convert each BlockDto into a managed Block entity
        List<Block> blocks = dormDto.getBlocks().stream()
                .map(blockDto -> {
                    // a) Try loading existing block
                    Block block = blockRepository
                            .findById(blockDto.getId())
                            .orElse(new Block());

                    block.setId(blockDto.getId());
                    block.setName(blockDto.getName());
                    block.setDorm(dorm);
                    return block;
                })
                .toList();

        // 3) Attach the blocks collection to the Dorm
        dorm.setBlocks(blocks);

        return dorm;
    }
}
