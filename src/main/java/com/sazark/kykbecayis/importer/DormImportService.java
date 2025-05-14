package com.sazark.kykbecayis.importer;

import com.sazark.kykbecayis.domain.entities.Block;
import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.domain.entities.enums.GenderType;
import com.sazark.kykbecayis.repositories.DormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DormImportService {

    private final DormRepository dormRepository;
    private final DormJsonReader dormJsonReader;

    public void importDormsFromJson() throws Exception {
        List<DormJsonDto> dorms = dormJsonReader.readDormJson();

        for (DormJsonDto dto : dorms) {
            Dorm dorm = mapDorm(dto);
            dormRepository.save(dorm);
        }
    }

    private Dorm mapDorm(DormJsonDto dto) {
        GenderType dormType = parseGenderType(dto.getType());

        List<Block> blocks = new ArrayList<>();
        for (BlockJsonDto blockDto : dto.getBlocks()) {
            try {
                GenderType blockType = parseGenderType(blockDto.getType());
                Block block = Block.builder()
                        .type(blockType)
                        .fullAddress(blockDto.getAddress())
                        .city(blockDto.getCity())
                        .build();
                blocks.add(block);
            } catch (IllegalArgumentException e) {
                System.err.println("Skipping block due to invalid gender type: " + blockDto.getType() + " in block: " + blockDto.getName());
            }
        }

        Dorm dorm = Dorm.builder()
                .name(dto.getName())
                .type(dormType)
                .phoneNumber(dto.getPhone())
                .fullAddress(dto.getAddress())
                .city(dto.getCity())
                .blocks(blocks)
                .build();

        blocks.forEach(b -> b.setDorm(dorm));
        return dorm;
    }

    private GenderType parseGenderType(String raw) {
        raw = raw.toLowerCase().trim();
        return switch (raw) {
            case "erkek" -> GenderType.MALE;
            case "kız" -> GenderType.FEMALE;
            case "karma" -> GenderType.HYBRID;
            default -> throw new IllegalArgumentException("Unknown gender type: " + raw);
        };
    }
}
