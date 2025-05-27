package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.misc.dto.PostingDto;
import com.sazark.kykbecayis.posting.PostingService;
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
    private ObjectMapper objectMapper;

    @Autowired
    private PostingService postingService;

    @TestConfiguration
    static class PostingControllerTestContextConfiguration {
        @Bean
        public PostingService postingService() {
            return Mockito.mock(PostingService.class);
        }
    }

    private PostingDto getSampleDto() {
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
    void testCreatePosting() throws Exception {
        PostingDto dto = getSampleDto();
        when(postingService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/postings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/postings/10"));
    }

    @Test
    void testGetAllPostings() throws Exception {
        when(postingService.findAll()).thenReturn(List.of(getSampleDto()));
        mockMvc.perform(get("/api/postings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    void testDelete_Posting_found() throws Exception {
        when(postingService.delete(10L)).thenReturn(true);
        mockMvc.perform(delete("/api/postings/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDelete_Posting_notFound() throws Exception {
        when(postingService.delete(10L)).thenReturn(false);
        mockMvc.perform(delete("/api/postings/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostingById_returnsPosting_whenFound() throws Exception {
        PostingDto dto = PostingDto.builder().id(1L).build();
        when(postingService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/postings").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getPostingById_returns404_whenNotFound() throws Exception {
        when(postingService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/postings").param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostings_withFilters_returnsFilteredList() throws Exception {
        PostingDto dto = PostingDto.builder().id(2L).build();
        when(postingService.filterPostings(5L, 10L, 15L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/postings")
                        .param("userId", "5")
                        .param("sourceDormId", "10")
                        .param("targetDormId", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void getAllPostings_returnsList_whenNoParams() throws Exception {
        PostingDto dto = PostingDto.builder().id(3L).build();
        when(postingService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/postings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    void getPostingCount_returnsCorrectCount() throws Exception {
        when(postingService.countPostings(5L, 10L, 15L)).thenReturn(7L);

        mockMvc.perform(get("/api/postings/count")
                        .param("userId", "5")
                        .param("sourceDormId", "10")
                        .param("targetDormId", "15"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }
}
