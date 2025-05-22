package com.sazark.kykbecayis.offer;

import com.sazark.kykbecayis.misc.dto.OfferDto;
import com.sazark.kykbecayis.misc.mapper.OfferMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    public OfferService(OfferRepository offerRepository, OfferMapper offerMapper) {
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
    }

    public OfferDto create(OfferDto offerDto) {
        Offer offer = offerMapper.toEntity(offerDto);
        Offer savedOffer = offerRepository.save(offer);
        return offerMapper.toDTO(savedOffer);
    }

    public OfferDto update(Long id, OfferDto offerDto) {
        if (!offerRepository.existsById(id)) {
            return null;
        }

        Offer offer = offerMapper.toEntity(offerDto);
        offer.setId(id);
        Offer savedOffer = offerRepository.save(offer);
        return offerMapper.toDTO(savedOffer);
    }

    public OfferDto findById(Long id) {
        Offer offer = offerRepository.findById(id).orElse(null);
        return offerMapper.toDTO(offer);
    }

    public List<OfferDto> findAll() {
        return offerRepository.findAll()
                .stream()
                .map(offerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean delete(Long id) {
        if (!offerRepository.existsById(id)) {
            return false;
        }
        offerRepository.deleteById(id);
        return true;
    }
}
