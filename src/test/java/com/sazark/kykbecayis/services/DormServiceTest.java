package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.core.enums.GenderType;
import com.sazark.kykbecayis.core.mapper.DormMapper;
import com.sazark.kykbecayis.housing.dorm.Dorm;
import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.housing.dorm.DormService;
import com.sazark.kykbecayis.housing.dto.DormDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class DormServiceTest {

    @Mock
    private DormRepository dormRepository;

    @Mock
    private DormMapper dormMapper;

    @InjectMocks
    private DormService dormService;

    private DormDto dto;
    private Dorm entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dto = DormDto.builder()
                .id(1L)
                .type(GenderType.MALE)
                .fullAddress("123 Dorm St")
                .city("DormCity")
                .name("DormName")
                .phoneNumber("555-1234")
                .blockIds(List.of(10L, 11L))
                .build();

        entity = Dorm.builder()
                .id(1L)
                .type(GenderType.MALE)
                .fullAddress("123 Dorm St")
                .city("DormCity")
                .name("DormName")
                .phoneNumber("555-1234")
                .build();
    }

    @Test
    void testCreate() {
        when(dormMapper.toEntity(dto)).thenReturn(entity);
        when(dormRepository.save(entity)).thenReturn(entity);
        when(dormMapper.toDTO(entity)).thenReturn(dto);

        DormDto result = dormService.create(dto);
        assertEquals(dto, result);
    }

    @Test
    void testUpdate_whenExists() {
        when(dormRepository.existsById(1L)).thenReturn(true);
        when(dormMapper.toEntity(dto)).thenReturn(entity);
        when(dormRepository.save(entity)).thenReturn(entity);
        when(dormMapper.toDTO(entity)).thenReturn(dto);

        DormDto result = dormService.update(1L, dto);
        assertEquals(dto, result);
    }

    @Test
    void testUpdate_whenNotExists() {
        when(dormRepository.existsById(1L)).thenReturn(false);

        DormDto result = dormService.update(1L, dto);
        assertNull(result);
    }

    @Test
    void testFindById_whenExists() {
        when(dormRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(dormMapper.toDTO(entity)).thenReturn(dto);

        DormDto result = dormService.findById(1L);
        assertEquals(dto, result);
    }

    @Test
    void testFindById_whenNotExists() {
        when(dormRepository.findById(1L)).thenReturn(Optional.empty());
        when(dormMapper.toDTO(null)).thenReturn(null);

        DormDto result = dormService.findById(1L);
        assertNull(result);
    }

    @Test
    void testFindAll() {
        List<Dorm> entities = List.of(entity);
        List<DormDto> dtos = List.of(dto);

        when(dormRepository.findAll()).thenReturn(entities);
        when(dormMapper.toDTO(entity)).thenReturn(dto);

        List<DormDto> result = dormService.findAll();
        assertEquals(dtos, result);
    }

    @Test
    void testDelete_whenExists() {
        when(dormRepository.existsById(1L)).thenReturn(true);
        doNothing().when(dormRepository).deleteById(1L);

        boolean result = dormService.delete(1L);
        assertTrue(result);
    }

    @Test
    void testDelete_whenNotExists() {
        when(dormRepository.existsById(1L)).thenReturn(false);

        boolean result = dormService.delete(1L);
        assertFalse(result);
    }
}
