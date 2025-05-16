package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.domain.dto.PostingDto;
import com.sazark.kykbecayis.services.PostingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostingController.class)
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
        Mockito.when(postingService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/postings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/postings/10"));
    }

    @Test
    void testGetPosting_found() throws Exception {
        Mockito.when(postingService.findById(10L)).thenReturn(getSampleDto());

        mockMvc.perform(get("/api/postings/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void testGetPosting_notFound() throws Exception {
        Mockito.when(postingService.findById(10L)).thenReturn(null);
        mockMvc.perform(get("/api/postings/10")).andExpect(status().isNotFound());
    }

    @Test
    void testGetAllPostings() throws Exception {
        Mockito.when(postingService.findAll()).thenReturn(List.of(getSampleDto()));
        mockMvc.perform(get("/api/postings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    void testDelete_Posting_found() throws Exception {
        Mockito.when(postingService.delete(10L)).thenReturn(true);
        mockMvc.perform(delete("/api/postings/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDelete_Posting_notFound() throws Exception {
        Mockito.when(postingService.delete(10L)).thenReturn(false);
        mockMvc.perform(delete("/api/postings/10"))
                .andExpect(status().isNotFound());
    }
}
