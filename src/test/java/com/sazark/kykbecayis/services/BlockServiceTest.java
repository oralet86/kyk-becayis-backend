package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.domain.dto.BlockDto;
import com.sazark.kykbecayis.domain.entities.Block;
import com.sazark.kykbecayis.domain.entities.enums.GenderType;
import com.sazark.kykbecayis.mappers.impl.BlockMapper;
import com.sazark.kykbecayis.repositories.BlockRepository;
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

class BlockServiceTest {

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private BlockMapper blockMapper;

    @InjectMocks
    private BlockService blockService;

    private BlockDto dto;
    private Block entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dto = BlockDto.builder()
                .id(1L)
                .type(GenderType.MALE)
                .fullAddress("123 Test St")
                .city("Testville")
                .dormId(2L)
                .build();

        entity = Block.builder()
                .id(1L)
                .type(GenderType.MALE)
                .fullAddress("123 Test St")
                .city("Testville")
                .build();
    }

    @Test
    void testCreate() {
        when(blockMapper.toEntity(dto)).thenReturn(entity);
        when(blockRepository.save(entity)).thenReturn(entity);
        when(blockMapper.toDTO(entity)).thenReturn(dto);

        BlockDto result = blockService.create(dto);
        assertEquals(dto, result);
    }

    @Test
    void testUpdate_whenExists() {
        when(blockRepository.existsById(1L)).thenReturn(true);
        when(blockMapper.toEntity(dto)).thenReturn(entity);
        when(blockRepository.save(entity)).thenReturn(entity);
        when(blockMapper.toDTO(entity)).thenReturn(dto);

        BlockDto result = blockService.update(1L, dto);
        assertEquals(dto, result);
    }

    @Test
    void testUpdate_whenNotExists() {
        when(blockRepository.existsById(1L)).thenReturn(false);

        BlockDto result = blockService.update(1L, dto);
        assertNull(result);
    }

    @Test
    void testFindById_whenExists() {
        when(blockRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(blockMapper.toDTO(entity)).thenReturn(dto);

        BlockDto result = blockService.findById(1L);
        assertEquals(dto, result);
    }

    @Test
    void testFindById_whenNotExists() {
        when(blockRepository.findById(1L)).thenReturn(Optional.empty());
        when(blockMapper.toDTO(null)).thenReturn(null);

        BlockDto result = blockService.findById(1L);
        assertNull(result);
    }

    @Test
    void testFindAll() {
        List<Block> entities = List.of(entity);
        List<BlockDto> dtos = List.of(dto);

        when(blockRepository.findAll()).thenReturn(entities);
        when(blockMapper.toDTO(entity)).thenReturn(dto);

        List<BlockDto> result = blockService.findAll();
        assertEquals(dtos, result);
    }

    @Test
    void testDelete_whenExists() {
        when(blockRepository.existsById(1L)).thenReturn(true);
        doNothing().when(blockRepository).deleteById(1L);

        boolean result = blockService.delete(1L);
        assertTrue(result);
    }

    @Test
    void testDelete_whenNotExists() {
        when(blockRepository.existsById(1L)).thenReturn(false);

        boolean result = blockService.delete(1L);
        assertFalse(result);
    }
}
