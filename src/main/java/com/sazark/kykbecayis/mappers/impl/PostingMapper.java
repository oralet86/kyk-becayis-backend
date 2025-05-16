package com.sazark.kykbecayis.mappers.impl;

import com.sazark.kykbecayis.domain.dto.PostingDto;
import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.domain.entities.Posting;
import com.sazark.kykbecayis.domain.entities.User;
import com.sazark.kykbecayis.mappers.Mapper;
import com.sazark.kykbecayis.repositories.DormRepository;
import com.sazark.kykbecayis.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class PostingMapper implements Mapper<Posting, PostingDto> {

    private final DormRepository dormRepository;
    private final UserRepository userRepository;

    public PostingMapper(DormRepository dormRepository, UserRepository userRepository) {
        this.dormRepository = dormRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PostingDto toDTO(Posting posting) {
        if (posting == null) return null;

        return PostingDto.builder()
                .id(posting.getId())
                .isValid(posting.getIsValid())
                .date(posting.getDate().toString())
                .userId(posting.getUser() != null ? posting.getUser().getId() : null)
                .sourceDormId(posting.getSourceDorm() != null ? posting.getSourceDorm().getId() : null)
                .targetDormIds(posting.getTargetDorms() != null
                        ? posting.getTargetDorms().stream().map(Dorm::getId).toList()
                        : new ArrayList<>())
                .build();
    }

    @Override
    public Posting toEntity(PostingDto dto) {
        if (dto == null) return null;

        User user = userRepository.findById(dto.getUserId()).orElse(null);
        Dorm sourceDorm = dormRepository.findById(dto.getSourceDormId()).orElse(null);

        return Posting.builder()
                .id(dto.getId())
                .isValid(dto.getIsValid())
                .date(LocalDate.parse(dto.getDate())) // Parse String to LocalDate
                .user(user)
                .sourceDorm(sourceDorm)
                .targetDorms(dto.getTargetDormIds() != null
                        ? dormRepository.findAllById(dto.getTargetDormIds())
                        : new ArrayList<>())
                .build();
    }
}