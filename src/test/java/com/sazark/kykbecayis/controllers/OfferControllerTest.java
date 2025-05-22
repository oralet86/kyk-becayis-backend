package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.domain.dto.OfferDto;
import com.sazark.kykbecayis.services.OfferService;
import org.junit.jupiter.api.DisplayName;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OfferService offerService;

    @TestConfiguration
    static class OfferControllerTestContextConfiguration {
        @Bean
        public OfferService offerService() {
            return Mockito.mock(OfferService.class);
        }
    }

    @Test
    @DisplayName("POST /api/offers - Success")
    void testCreateOffer_Success() throws Exception {
        OfferDto input = OfferDto.builder().postingId(1L).senderId(2L).build();
        OfferDto created = OfferDto.builder().id(1L).postingId(1L).senderId(2L).created(LocalDateTime.now()).build();

        Mockito.when(offerService.create(any(OfferDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/offers/1"))
                .andExpect(content().json(objectMapper.writeValueAsString(created)));
    }

    @Test
    @DisplayName("GET /api/offers/{id} - Found")
    void testGetOfferById_Found() throws Exception {
        OfferDto offer = OfferDto.builder().id(1L).postingId(1L).senderId(2L).build();
        Mockito.when(offerService.findById(1L)).thenReturn(offer);

        mockMvc.perform(get("/api/offers/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(offer)));
    }

    @Test
    @DisplayName("GET /api/offers/{id} - Not Found")
    void testGetOfferById_NotFound() throws Exception {
        Mockito.when(offerService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/offers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/offers - Empty List")
    void testGetAllOffers_Empty() throws Exception {
        Mockito.when(offerService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/offers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/offers - Non-empty List")
    void testGetAllOffers_NonEmpty() throws Exception {
        OfferDto o1 = OfferDto.builder().id(1L).postingId(1L).senderId(2L).build();
        OfferDto o2 = OfferDto.builder().id(2L).postingId(1L).senderId(3L).build();
        List<OfferDto> offers = List.of(o1, o2);

        Mockito.when(offerService.findAll()).thenReturn(offers);

        mockMvc.perform(get("/api/offers"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(offers)));
    }

    @Test
    @DisplayName("PUT /api/offers/{id} - Success")
    void testUpdateOffer_Success() throws Exception {
        OfferDto input = OfferDto.builder().postingId(1L).senderId(2L).build();
        OfferDto updated = OfferDto.builder().id(1L).postingId(1L).senderId(2L).build();

        Mockito.when(offerService.update(eq(1L), any(OfferDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/offers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
    }

    @Test
    @DisplayName("PUT /api/offers/{id} - Not Found")
    void testUpdateOffer_NotFound() throws Exception {
        OfferDto input = OfferDto.builder().postingId(99L).senderId(88L).build();
        Mockito.when(offerService.update(eq(100L), any(OfferDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/offers/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/offers/{id} - Success")
    void testDeleteOffer_Success() throws Exception {
        Mockito.when(offerService.delete(3L)).thenReturn(true);

        mockMvc.perform(delete("/api/offers/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/offers/{id} - Not Found")
    void testDeleteOffer_NotFound() throws Exception {
        Mockito.when(offerService.delete(404L)).thenReturn(false);

        mockMvc.perform(delete("/api/offers/404"))
                .andExpect(status().isNotFound());
    }
}
