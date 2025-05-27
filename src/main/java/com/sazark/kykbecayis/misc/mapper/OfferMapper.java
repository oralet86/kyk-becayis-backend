package com.sazark.kykbecayis.misc.mapper;

import com.sazark.kykbecayis.misc.request.OfferCreateRequest;
import com.sazark.kykbecayis.offer.Offer;
import com.sazark.kykbecayis.misc.dto.OfferDto;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.posting.PostingRepository;
import com.sazark.kykbecayis.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class OfferMapper {

    private final PostingRepository postingRepository;
    private final UserRepository userRepository;

    public OfferMapper(PostingRepository postingRepository, UserRepository userRepository) {
        this.postingRepository = postingRepository;
        this.userRepository = userRepository;
    }

    public OfferDto toDTO(Offer offer) {
        if (offer == null) return null;

        return OfferDto.builder()
                .id(offer.getId())
                .postingId(offer.getPosting() != null ? offer.getPosting().getId() : null)
                .senderId(offer.getSender() != null ? offer.getSender().getId() : null)
                .status(offer.getStatus())
                .created(offer.getCreated())
                .build();
    }

    public Offer toEntity(OfferDto dto) {
        if (dto == null) return null;

        Posting posting = postingRepository.findById(dto.getPostingId()).orElse(null);
        User sender = userRepository.findById(dto.getSenderId()).orElse(null);

        return Offer.builder()
                .id(dto.getId())
                .posting(posting)
                .sender(sender)
                .status(dto.getStatus())
                .created(dto.getCreated())
                .build();
    }

    public Offer toEntity(OfferCreateRequest offerCreateRequest) {
        if (offerCreateRequest == null) return null;

        Posting posting = postingRepository.findById(offerCreateRequest.getPostingId()).orElse(null);
        User sender = userRepository.findById(offerCreateRequest.getSenderId()).orElse(null);

        return Offer.builder()
                .posting(posting)
                .sender(sender)
                .build();
    }
}
