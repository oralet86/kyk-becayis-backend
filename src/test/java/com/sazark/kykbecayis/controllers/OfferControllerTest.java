package com.sazark.kykbecayis.controllers;

import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.misc.dto.OfferDto;
import com.sazark.kykbecayis.offer.OfferService;
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
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfferService offerService;

    @Test
    void getOfferById_returnsOffer_whenFound() throws Exception {
        OfferDto offer = OfferDto.builder().id(1L).build();
        when(offerService.findById(1L)).thenReturn(offer);

        mockMvc.perform(get("/api/offers").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getOfferById_returns404_whenNotFound() throws Exception {
        when(offerService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/offers").param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOffersByPosting_returnsMatchingOffers() throws Exception {
        OfferDto offer = OfferDto.builder().id(2L).build();
        when(offerService.filterOffers(5L, null, null)).thenReturn(List.of(offer));

        mockMvc.perform(get("/api/offers").param("posting", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void getOffersBySenderId_returnsMatchingOffers() throws Exception {
        OfferDto offer = OfferDto.builder().id(3L).build();
        when(offerService.filterOffers(null, 7L, null)).thenReturn(List.of(offer));

        mockMvc.perform(get("/api/offers").param("senderId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    void getOffersBySenderUid_returnsMatchingOffers() throws Exception {
        OfferDto offer = OfferDto.builder().id(4L).build();
        when(offerService.filterOffers(null, null, "firebase-uid")).thenReturn(List.of(offer));

        mockMvc.perform(get("/api/offers").param("senderUid", "firebase-uid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4));
    }

    @Test
    void getAllOffers_returnsAll_whenNoParams() throws Exception {
        OfferDto offer = OfferDto.builder().id(5L).build();
        when(offerService.findAll()).thenReturn(List.of(offer));

        mockMvc.perform(get("/api/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5));
    }
}
