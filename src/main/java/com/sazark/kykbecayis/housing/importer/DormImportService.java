package com.sazark.kykbecayis.housing.importer;

import com.sazark.kykbecayis.core.enums.GenderType;
import com.sazark.kykbecayis.housing.block.Block;
import com.sazark.kykbecayis.housing.dorm.Dorm;
import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.housing.dto.BlockJsonDto;
import com.sazark.kykbecayis.housing.dto.DormJsonDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Profile("!test")
@Service
@RequiredArgsConstructor
public class DormImportService {
    private static final Logger logger = LoggerFactory.getLogger(DormImportService.class);

    private final DormRepository dormRepository;
    private final DormJsonReader dormJsonReader;

    @Transactional
    public void importDormsFromJson() {
        List<DormJsonDto> dorms = dormJsonReader.readDormJson();
        saveDorms(dorms);
    }

    @Transactional
    protected void saveDorms(List<DormJsonDto> dorms) {
        Set<String> jsonDormKeys = dorms.stream()
                .map(dto -> dto.getName() + "|" + dto.getCity())
                .collect(Collectors.toSet());

        List<Dorm> allDormsInDb = dormRepository.findAll();

        // Remove dorms not present in the JSON
        for (Dorm dbDorm : allDormsInDb) {
            String key = dbDorm.getName() + "|" + dbDorm.getCity();
            if (!jsonDormKeys.contains(key)) {
                dormRepository.delete(dbDorm);
                logger.info("Dorm removed: {} ({})", dbDorm.getName(), dbDorm.getCity());
            }
        }

        // Add/update dorms
        for (DormJsonDto dto : dorms) {
            Dorm dorm = mapDorm(dto);
            Optional<Dorm> existing = dormRepository.findByNameAndCity(dto.getName(), dto.getCity());

            if (existing.isEmpty()) {
                dormRepository.save(dorm);
                logger.info("Dorm added: {} ({})", dorm.getName(), dorm.getCity());
            } else {
                Dorm dbDorm = existing.get();
                boolean changed = updateDormIfDifferent(dbDorm, dorm);
                if (changed) {
                    dormRepository.save(dbDorm);
                    logger.info("Dorm updated: {} ({})", dbDorm.getName(), dbDorm.getCity());
                }
            }
        }
    }

    @Transactional
    protected boolean updateDormIfDifferent(Dorm existing, Dorm incoming) {
        boolean updated = false;

        if (applyDormChanges(existing, incoming)) {
            System.out.printf("Updated dorm: %s (%s)%n", existing.getName(), existing.getCity());
            updated = true;
        }

        Map<String, Block> existingBlockMap = existing.getBlocks().stream()
                .collect(Collectors.toMap(Block::getName, b -> b));

        for (Block incomingBlock : incoming.getBlocks()) {
            Block existingBlock = existingBlockMap.get(incomingBlock.getName());

            if (existingBlock == null) {
                incomingBlock.setDorm(existing);
                existing.getBlocks().add(incomingBlock);
                System.out.printf("Added block: %s to dorm: %s%n", incomingBlock.getName(), existing.getName());
                updated = true;
            } else if (applyBlockChanges(existingBlock, incomingBlock)) {
                System.out.printf("Updated block: %s in dorm: %s%n", existingBlock.getName(), existing.getName());
                updated = true;
            }
        }

        return updated;
    }

    private boolean applyDormChanges(Dorm existing, Dorm incoming) {
        boolean changed = false;

        if (!Objects.equals(existing.getPhoneNumber(), incoming.getPhoneNumber())) {
            existing.setPhoneNumber(incoming.getPhoneNumber());
            changed = true;
        }
        if (!Objects.equals(existing.getFullAddress(), incoming.getFullAddress())) {
            existing.setFullAddress(incoming.getFullAddress());
            changed = true;
        }
        if (!Objects.equals(existing.getLocation(), incoming.getLocation())) {
            existing.setLocation(incoming.getLocation());
            changed = true;
        }
        if (!Objects.equals(existing.getType(), incoming.getType())) {
            existing.setType(incoming.getType());
            changed = true;
        }

        return changed;
    }

    private boolean applyBlockChanges(Block existing, Block incoming) {
        boolean changed = false;

        if (!Objects.equals(existing.getType(), incoming.getType())) {
            existing.setType(incoming.getType());
            changed = true;
        }
        if (!Objects.equals(existing.getCity(), incoming.getCity())) {
            existing.setCity(incoming.getCity());
            changed = true;
        }
        if (!Objects.equals(existing.getFullAddress(), incoming.getFullAddress())) {
            existing.setFullAddress(incoming.getFullAddress());
            changed = true;
        }
        if (!Objects.equals(existing.getLocation(), incoming.getLocation())) {
            existing.setLocation(incoming.getLocation());
            changed = true;
        }

        return changed;
    }

    private Dorm mapDorm(DormJsonDto dto) {
        GenderType dormType = parseGenderType(dto.getType());

        List<Block> blocks = new ArrayList<>();
        for (BlockJsonDto blockDto : dto.getBlocks()) {
            try {
                GenderType blockType = parseGenderType(blockDto.getType());
                Block block = Block.builder()
                        .name(blockDto.getName())
                        .type(blockType)
                        .fullAddress(blockDto.getAddress())
                        .location(blockDto.getLocation())
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
                .location(dto.getLocation())
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