package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.domain.dto.BlockDto;
import com.sazark.kykbecayis.domain.entities.enums.GenderType;
import com.sazark.kykbecayis.services.BlockService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class BlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BlockService blockService;

    private BlockDto getSampleDto() {
        return BlockDto.builder()
                .id(1L)
                .type(GenderType.MALE)
                .fullAddress("123 Test St")
                .city("Testville")
                .dormId(5L)
                .build();
    }

    @Test
    void testCreateBlock() throws Exception {
        BlockDto dto = getSampleDto();

        Mockito.when(blockService.create(any(BlockDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/blocks/1"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetBlockById_found() throws Exception {
        BlockDto dto = getSampleDto();

        Mockito.when(blockService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/blocks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.city").value("Testville"));
    }

    @Test
    void testGetBlockById_notFound() throws Exception {
        Mockito.when(blockService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/blocks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllBlocks() throws Exception {
        BlockDto dto = getSampleDto();

        Mockito.when(blockService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/blocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testFilterBlocksByDormId() throws Exception {
        BlockDto dto = getSampleDto();

        Mockito.when(blockService.findByDormId(5L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/blocks/filter")
                        .param("dormId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].dormId").value(5L));
    }


    @Test
    void testUpdateBlock_found() throws Exception {
        BlockDto dto = getSampleDto();

        Mockito.when(blockService.update(eq(1L), any(BlockDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/blocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateBlock_notFound() throws Exception {
        Mockito.when(blockService.update(eq(1L), any(BlockDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/blocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBlock_found() throws Exception {
        Mockito.when(blockService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/blocks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBlock_notFound() throws Exception {
        Mockito.when(blockService.delete(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/blocks/1"))
                .andExpect(status().isNotFound());
    }
}
