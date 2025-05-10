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
    private final DormCsvReader dormCsvReader;

    public void importDormsFromCsv() throws Exception {
        List<String[]> rows = dormCsvReader.readDormCsv();

        for (String[] parts : rows) {
            Dorm dorm = parseDormLine(parts);
            dormRepository.save(dorm);
        }
    }

    private Dorm parseDormLine(String[] parts) {
        String name = parts[0].trim();
        GenderType dormType = parseGenderType(parts[1]);
        String phone = extractValue(parts[2]);
        String fax = extractValue(parts[3]);
        String fullAddress = extractValue(parts[4]);

        String city = extractCity(fullAddress);
        String province = extractProvince(fullAddress);

        List<Block> blocks = new ArrayList<>();
        for (int i = 5; i + 2 < parts.length; i += 3) {
            String blockName = parts[i].trim();
            String genderRaw = parts[i + 1].trim();
            String address = parts[i + 2].trim();

            try {
                GenderType blockType = parseGenderType(genderRaw);
                Block block = Block.builder()
                        .type(blockType)
                        .fullAddress(address)
                        .city(extractCity(address))
                        .province(extractProvince(address))
                        .build();
                blocks.add(block);
            } catch (IllegalArgumentException e) {
                System.err.println("Skipping block due to invalid gender type: " + genderRaw + " in block: " + blockName);
            }
        }


        Dorm dorm = Dorm.builder()
                .name(name)
                .type(dormType)
                .phoneNumber(phone)
                .faxNumber(fax)
                .fullAddress(fullAddress)
                .city(city)
                .province(province)
                .blocks(blocks)
                .build();

        blocks.forEach(b -> b.setDorm(dorm));
        return dorm;
    }

    private String extractValue(String part) {
        return part.contains(":") ? part.split(":", 2)[1].trim() : part.trim();
    }

    private GenderType parseGenderType(String raw) {
        raw = raw.toLowerCase().replace("tipi", "")
                .replace(":", "")
                .trim();
        return switch (raw) {
            case "erkek" -> GenderType.MALE;
            case "kız" -> GenderType.FEMALE;
            case "karma" -> GenderType.HYBRID;
            default -> throw new IllegalArgumentException("Unknown gender type: " + raw);
        };
    }

    private String extractCity(String address) {
        if (address.contains("/")) {
            String[] parts = address.split("/");
            return parts[0].trim().replaceAll(".*\\s", "");
        }
        return "";
    }

    private String extractProvince(String address) {
        if (address.contains("/")) {
            String[] parts = address.split("/");
            return parts[1].trim();
        }
        return "";
    }
}
