package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.misc.dto.OfferDto;
import com.sazark.kykbecayis.misc.request.OfferCreateRequest;
import com.sazark.kykbecayis.offer.Offer;
import com.sazark.kykbecayis.misc.mapper.OfferMapper;
import com.sazark.kykbecayis.offer.OfferRepository;
import com.sazark.kykbecayis.offer.OfferService;
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
        OfferCreateRequest request = OfferCreateRequest.builder()
                .postingId(1L)
                .senderId(2L)
                .build();

        // Mocked entity and result objects
        Offer offer = new Offer(); // The offer to be saved
        Offer savedOffer = new Offer(); // What the repository returns after saving
        savedOffer.setId(1L);
        savedOffer.setCreated(LocalDateTime.now());

        OfferDto outputDto = OfferDto.builder()
                .id(1L)
                .postingId(1L)
                .senderId(2L)
                .created(savedOffer.getCreated())
                .build();

        // Act: mock behavior
        when(offerMapper.toEntity(request)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(savedOffer);
        when(offerMapper.toDTO(savedOffer)).thenReturn(outputDto);

        // Call the service
        OfferDto result = offerService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getPostingId());
        assertEquals(2L, result.getSenderId());
        assertNotNull(result.getCreated());
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
