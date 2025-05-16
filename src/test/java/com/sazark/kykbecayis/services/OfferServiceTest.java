package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.domain.dto.OfferDto;
import com.sazark.kykbecayis.domain.entities.Offer;
import com.sazark.kykbecayis.mappers.impl.OfferMapper;
import com.sazark.kykbecayis.repositories.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferServiceTest {

    private OfferRepository offerRepository;
    private OfferMapper offerMapper;
    private OfferService offerService;

    @BeforeEach
    void setup() {
        offerRepository = mock(OfferRepository.class);
        offerMapper = mock(OfferMapper.class);
        offerService = new OfferService(offerRepository, offerMapper);
    }

    @Test
    void createOffer_savesAndReturnsDto() {
        OfferDto inputDto = OfferDto.builder().postingId(1L).senderId(2L).build();
        Offer offer = new Offer();
        Offer savedOffer = new Offer();
        OfferDto outputDto = OfferDto.builder().id(1L).postingId(1L).senderId(2L).created(LocalDateTime.now()).build();

        when(offerMapper.toEntity(inputDto)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(savedOffer);
        when(offerMapper.toDTO(savedOffer)).thenReturn(outputDto);

        OfferDto result = offerService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_returnsMappedDto() {
        Offer offer = new Offer();
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(offerMapper.toDTO(offer)).thenReturn(OfferDto.builder().id(1L).build());

        OfferDto result = offerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void delete_existingOffer_returnsTrue() {
        when(offerRepository.existsById(1L)).thenReturn(true);
        boolean result = offerService.delete(1L);
        assertTrue(result);
        verify(offerRepository).deleteById(1L);
    }

    @Test
    void delete_nonexistentOffer_returnsFalse() {
        when(offerRepository.existsById(999L)).thenReturn(false);
        boolean result = offerService.delete(999L);
        assertFalse(result);
    }
}
