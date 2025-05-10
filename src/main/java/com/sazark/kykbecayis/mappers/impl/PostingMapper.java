package com.sazark.kykbecayis.mappers.impl;

import com.sazark.kykbecayis.domain.dto.PostingDto;
import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.domain.entities.Posting;
import com.sazark.kykbecayis.mappers.Mapper;
import com.sazark.kykbecayis.repositories.DormRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PostingMapper implements Mapper<Posting, PostingDto> {

    private final DormRepository dormRepository;

    public PostingMapper(DormRepository dormRepository) {
        this.dormRepository = dormRepository;
    }

    @Override
    public PostingDto toDTO(Posting posting) {
        if (posting == null) return null;

        return PostingDto.builder()
                .id(posting.getId())
                .user(posting.getUser())
                .sourceDorm(posting.getSourceDorm())
                .targetDormIds(posting.getTargetDorms() != null
                        ? posting.getTargetDorms().stream().map(Dorm::getId).toList()
                        : new ArrayList<>())
                .build();
    }

    @Override
    public Posting toEntity(PostingDto postingDto) {
        if (postingDto == null) return null;

        return Posting.builder()
                .id(postingDto.getId())
                .user(postingDto.getUser())
                .sourceDorm(postingDto.getSourceDorm())
                .targetDorms(postingDto.getTargetDormIds() != null
                        ? dormRepository.findAllById(postingDto.getTargetDormIds())
                        : new ArrayList<>())
                .build();
    }
}
