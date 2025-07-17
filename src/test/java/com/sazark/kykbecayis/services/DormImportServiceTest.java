package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.core.enums.GenderType;
import com.sazark.kykbecayis.housing.block.Block;
import com.sazark.kykbecayis.housing.dorm.Dorm;
import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.housing.dto.BlockJsonDto;
import com.sazark.kykbecayis.housing.dto.DormJsonDto;
import com.sazark.kykbecayis.housing.importer.DormImportService;
import com.sazark.kykbecayis.housing.importer.DormJsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class DormImportServiceTest {

    @Mock
    private DormRepository dormRepository;

    @Mock
    private DormJsonReader dormJsonReader;

    @InjectMocks
    private DormImportService dormImportService;

    private DormJsonDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dto = DormJsonDto.builder()
                .name("Test Dorm")
                .type("erkek")
                .phone("123456789")
                .city("Ankara")
                .address("Main Street")
                .location("39.9,32.8")
                .blocks(Collections.singletonList(
                        BlockJsonDto.builder()
                                .name("A Block")
                                .type("erkek")
                                .city("Ankara")
                                .address("Block A")
                                .location("39.91,32.81")
                                .build()
                ))
                .build();
    }

    public GenderType parseGenderType(String raw) {
        raw = raw.toLowerCase().trim();
        return switch (raw) {
            case "erkek" -> GenderType.MALE;
            case "kız" -> GenderType.FEMALE;
            case "karma" -> GenderType.HYBRID;
            default -> throw new IllegalArgumentException("Unknown gender type: " + raw);
        };
    }

    @Test
    void loadsDormsFromJsonFile() {
        when(dormJsonReader.readDormJson()).thenReturn(List.of(dto));
        when(dormRepository.findByNameAndCity(dto.getName(), dto.getCity())).thenReturn(Optional.empty());

        dormImportService.importDormsFromJson();

        verify(dormJsonReader).readDormJson();
        verify(dormRepository).save(any());
    }

    @Test
    void savesDorm_whenNotExists() {
        when(dormJsonReader.readDormJson()).thenReturn(List.of(dto));
        when(dormRepository.findByNameAndCity(dto.getName(), dto.getCity())).thenReturn(Optional.empty());

        dormImportService.importDormsFromJson();

        verify(dormRepository).save(argThat(d -> d.getName().equals("Test Dorm")));
    }

    @Test
    void updatesDorm_whenFieldDiffers() {
        Dorm existing = Dorm.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .phoneNumber("DIFFERENT")
                .fullAddress(dto.getAddress())
                .location(dto.getLocation())
                .type(parseGenderType(dto.getType()))
                .blocks(new ArrayList<>())
                .build();

        when(dormJsonReader.readDormJson()).thenReturn(List.of(dto));
        when(dormRepository.findByNameAndCity(dto.getName(), dto.getCity())).thenReturn(Optional.of(existing));

        dormImportService.importDormsFromJson();

        verify(dormRepository).save(argThat(d -> d.getPhoneNumber().equals(dto.getPhone())));
    }

    @Test
    void skipsDormSave_whenUnchanged() {
        Dorm existing = Dorm.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .phoneNumber(dto.getPhone())
                .fullAddress(dto.getAddress())
                .location(dto.getLocation())
                .type(parseGenderType(dto.getType()))
                .blocks(Collections.singletonList(
                        Block.builder()
                                .name("A Block")
                                .city("Ankara")
                                .type(parseGenderType("erkek"))
                                .fullAddress("Block A")
                                .location("39.91,32.81")
                                .build()
                ))
                .build();

        when(dormJsonReader.readDormJson()).thenReturn(List.of(dto));
        when(dormRepository.findByNameAndCity(dto.getName(), dto.getCity())).thenReturn(Optional.of(existing));

        dormImportService.importDormsFromJson();

        verify(dormRepository, never()).save(any());
    }

    @Test
    void addsBlock_whenBlockNotFound() {
        Dorm existing = Dorm.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .phoneNumber(dto.getPhone())
                .fullAddress(dto.getAddress())
                .location(dto.getLocation())
                .type(parseGenderType(dto.getType()))
                .blocks(new ArrayList<>())
                .build();

        when(dormJsonReader.readDormJson()).thenReturn(List.of(dto));
        when(dormRepository.findByNameAndCity(dto.getName(), dto.getCity())).thenReturn(Optional.of(existing));

        dormImportService.importDormsFromJson();

        verify(dormRepository).save(argThat(d -> d.getBlocks().size() == 1));
    }

    @Test
    void updatesBlock_whenFieldDiffers() {
        Dorm existing = Dorm.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .phoneNumber(dto.getPhone())
                .fullAddress(dto.getAddress())
                .location(dto.getLocation())
                .type(parseGenderType(dto.getType()))
                .blocks(Collections.singletonList(
                        Block.builder()
                                .name("A Block")
                                .city("DIFFERENT")
                                .type(parseGenderType("erkek"))
                                .fullAddress("Block A")
                                .location("39.91,32.81")
                                .build()
                ))
                .build();

        when(dormJsonReader.readDormJson()).thenReturn(List.of(dto));
        when(dormRepository.findByNameAndCity(dto.getName(), dto.getCity())).thenReturn(Optional.of(existing));

        dormImportService.importDormsFromJson();

        verify(dormRepository).save(argThat(d -> d.getBlocks().get(0).getCity().equals("Ankara")));
    }

    @Test
    void skipsBlockUpdate_whenUnchanged() {
        Dorm existing = Dorm.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .phoneNumber(dto.getPhone())
                .fullAddress(dto.getAddress())
                .location(dto.getLocation())
                .type(parseGenderType(dto.getType()))
                .blocks(Collections.singletonList(
                        Block.builder()
                                .name("A Block")
                                .city("Ankara")
                                .type(parseGenderType("erkek"))
                                .fullAddress("Block A")
                                .location("39.91,32.81")
                                .build()
                ))
                .build();

        when(dormJsonReader.readDormJson()).thenReturn(List.of(dto));
        when(dormRepository.findByNameAndCity(dto.getName(), dto.getCity())).thenReturn(Optional.of(existing));

        dormImportService.importDormsFromJson();

        verify(dormRepository, never()).save(any());
    }

    @Test
    void removesDorm_whenNotInJson() {
        DormJsonDto jsonDorm = dto;

        Dorm orphanDorm = Dorm.builder()
                .name("Orphan Dorm")
                .city("Istanbul")
                .phoneNumber("000")
                .fullAddress("Orphan Street")
                .location("41.0,29.0")
                .type(GenderType.MALE)
                .blocks(Collections.emptyList())
                .build();

        when(dormJsonReader.readDormJson()).thenReturn(List.of(jsonDorm));
        when(dormRepository.findByNameAndCity(jsonDorm.getName(), jsonDorm.getCity())).thenReturn(Optional.empty());
        when(dormRepository.findAll()).thenReturn(List.of(orphanDorm));

        dormImportService.importDormsFromJson();

        verify(dormRepository).delete(argThat((Dorm d) -> d.getName().equals("Orphan Dorm") && d.getCity().equals("Istanbul")));
    }
}