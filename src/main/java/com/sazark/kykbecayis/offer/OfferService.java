package com.sazark.kykbecayis.offer;

import com.sazark.kykbecayis.misc.dto.OfferDto;
import com.sazark.kykbecayis.misc.mapper.OfferMapper;
import com.sazark.kykbecayis.misc.request.OfferCreateRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    public OfferService(OfferRepository offerRepository, OfferMapper offerMapper) {
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
    }

    public OfferDto create(OfferCreateRequest offerCreateRequest) {
        Offer offer = offerMapper.toEntity(offerCreateRequest);
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
        return offerRepository.findById(id)
                .map(offerMapper::toDTO)
                .orElse(null);
    }

    public List<OfferDto> findAll() {
        return offerRepository.findAll().stream()
                .map(offerMapper::toDTO)
                .toList();
    }

    public boolean delete(Long id) {
        if (!offerRepository.existsById(id)) {
            return false;
        }
        offerRepository.deleteById(id);
        return true;
    }

    public List<OfferDto> filterOffers(Long postingId, Long senderId, String senderUid) {
        return offerRepository.findAll((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (postingId != null) {
                        predicates.add(cb.equal(root.get("posting").get("id"), postingId));
                    }
                    if (senderId != null) {
                        predicates.add(cb.equal(root.get("sender").get("id"), senderId));
                    }
                    if (senderUid != null) {
                        predicates.add(cb.equal(root.get("sender").get("firebaseUID"), senderUid));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                }).stream()
                .map(offerMapper::toDTO)
                .toList();
    }

}
