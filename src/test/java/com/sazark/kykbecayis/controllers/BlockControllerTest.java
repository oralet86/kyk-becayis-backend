package com.sazark.kykbecayis.controllers;

import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.misc.dto.BlockDto;
import com.sazark.kykbecayis.block.BlockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class BlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlockService blockService;

    @Test
    void getBlockById_returnsBlock_whenIdProvided() throws Exception {
        BlockDto block = BlockDto.builder().id(1L).name("A Block").build();
        when(blockService.findById(1L)).thenReturn(block);

        mockMvc.perform(get("/api/blocks").param("blockId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBlockById_returns404_whenNotFound() throws Exception {
        when(blockService.findById(123L)).thenReturn(null);

        mockMvc.perform(get("/api/blocks").param("blockId", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBlocksByDormId_returnsFilteredBlocks() throws Exception {
        BlockDto block = BlockDto.builder().id(2L).build();
        when(blockService.findByDormId(10L)).thenReturn(List.of(block));

        mockMvc.perform(get("/api/blocks").param("dormId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void getAllBlocks_returnsAll_whenNoParams() throws Exception {
        BlockDto block = BlockDto.builder().id(3L).build();
        when(blockService.findAll()).thenReturn(List.of(block));

        mockMvc.perform(get("/api/blocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }
}
