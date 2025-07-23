package com.sazark.kykbecayis.controllers;

import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.housing.dorm.DormService;
import com.sazark.kykbecayis.housing.dto.DormDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class DormControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DormService dormService;

    @Test
    void getDorms_returnsAll() throws Exception {
        when(dormService.findLastModifiedTime())
                .thenReturn(Instant.EPOCH);

        when(dormService.findAll())
                .thenReturn(List.of(DormDto.builder().id(3L).build()));

        mockMvc.perform(get("/api/dorms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }
}
