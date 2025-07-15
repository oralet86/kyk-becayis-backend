package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.posting.PostingService;
import com.sazark.kykbecayis.posting.dto.PostingDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class PostingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private PostingService postingService;

    /* Helpers */
    private PostingDto sample() {
        return PostingDto.builder()
                .id(10L)
                .userId(1L)
                .sourceDormId(1L)
                .targetDormIds(List.of(2L))
                .isValid(true)
                .date("2025-05-16")
                .build();
    }

    @Test
    void createPosting_returns201() throws Exception {
        when(postingService.create(any())).thenReturn(sample());

        mockMvc.perform(post("/api/postings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sample())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/postings/10"));
    }

    /* CRUD */

    @Test
    void getAllPostings_returnsList() throws Exception {
        when(postingService.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/postings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void deletePosting_found_returns204() throws Exception {
        when(postingService.delete(10L)).thenReturn(true);

        mockMvc.perform(delete("/api/postings/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePosting_missing_returns404() throws Exception {
        when(postingService.delete(10L)).thenReturn(false);

        mockMvc.perform(delete("/api/postings/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostingById_returnsEntity() throws Exception {
        when(postingService.findById(1L))
                .thenReturn(PostingDto.builder().id(1L).build());

        mockMvc.perform(get("/api/postings").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getPostingById_missing_returns404() throws Exception {
        when(postingService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/postings").param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void filterPostings_returnsList() throws Exception {
        when(postingService.filterPostings(5L, 10L, 15L))
                .thenReturn(List.of(PostingDto.builder().id(2L).build()));

        mockMvc.perform(get("/api/postings")
                        .param("userId", "5")
                        .param("sourceDormId", "10")
                        .param("targetDormId", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void countPostings_returnsNumericString() throws Exception {
        when(postingService.countPostings(5L, 10L, 15L)).thenReturn(7L);

        mockMvc.perform(get("/api/postings/count")
                        .param("userId", "5")
                        .param("sourceDormId", "10")
                        .param("targetDormId", "15"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @TestConfiguration
    static class Cfg {
        @Bean
        PostingService postingService() {
            return Mockito.mock(PostingService.class);
        }
    }
}
