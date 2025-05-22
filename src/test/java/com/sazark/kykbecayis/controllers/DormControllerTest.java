package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.domain.dto.DormDto;
import com.sazark.kykbecayis.domain.entities.enums.GenderType;
import com.sazark.kykbecayis.services.DormService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class DormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DormService dormService;

    @TestConfiguration
    static class DormControllerTestContextConfiguration {
        @Bean
        public DormService dormService() {
            return Mockito.mock(DormService.class);
        }
    }

    private DormDto dto;

    @BeforeEach
    void setUp() {
        dto = DormDto.builder()
                .id(1L)
                .type(GenderType.MALE)
                .fullAddress("123 Dorm St")
                .city("DormCity")
                .name("DormName")
                .phoneNumber("555-1234")
                .build();
    }

    @Test
    void testCreateDorm() throws Exception {
        when(dormService.create(any(DormDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/dorms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/postings/" + dto.getId()))
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    void testGetDormById_found() throws Exception {
        when(dormService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/dorms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    void testGetDormById_notFound() throws Exception {
        when(dormService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/dorms/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDorms() throws Exception {
        List<DormDto> dorms = List.of(dto);
        when(dormService.findAll()).thenReturn(dorms);

        mockMvc.perform(get("/api/dorms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(dto.getId()));
    }

    @Test
    void testFilterDorms_withMultipleCriteria() throws Exception {
        DormDto dto = DormDto.builder()
                .id(1L)
                .type(GenderType.MALE)
                .city("Ankara")
                .name("Mehmetçik Yurdu")
                .fullAddress("Some address")
                .build();

        Mockito.when(dormService.filterDorms("MALE", "anka", "mehmet")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/dorms/filter")
                        .param("type", "MALE")
                        .param("city", "anka")
                        .param("name", "mehmet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].city").value("Ankara"))
                .andExpect(jsonPath("$[0].type").value("MALE"))
                .andExpect(jsonPath("$[0].name").value("Mehmetçik Yurdu"));
    }

    @Test
    void testUpdateDorm_found() throws Exception {
        when(dormService.update(eq(1L), any(DormDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/dorms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    void testUpdateDorm_notFound() throws Exception {
        when(dormService.update(eq(1L), any(DormDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/dorms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteDorm_found() throws Exception {
        when(dormService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/dorms/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDorm_notFound() throws Exception {
        when(dormService.delete(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/dorms/1"))
                .andExpect(status().isNotFound());
    }
}
