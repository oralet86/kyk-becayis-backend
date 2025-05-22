package com.sazark.kykbecayis.misc.mapper;

import com.sazark.kykbecayis.offer.Offer;
import com.sazark.kykbecayis.misc.dto.OfferDto;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.misc.Mapper;
import com.sazark.kykbecayis.posting.PostingRepository;
import com.sazark.kykbecayis.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class OfferMapper implements Mapper<Offer, OfferDto> {

    private final PostingRepository postingRepository;
    private final UserRepository userRepository;

    public OfferMapper(PostingRepository postingRepository, UserRepository userRepository) {
        this.postingRepository = postingRepository;
        this.userRepository = userRepository;
    }

    @Override
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

    @Override
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
}
