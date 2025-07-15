package com.sazark.kykbecayis.core.mapper;

import com.sazark.kykbecayis.housing.dorm.Dorm;
import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.posting.dto.PostingCreateRequest;
import com.sazark.kykbecayis.posting.dto.PostingDto;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PostingMapper {

    private final DormRepository dormRepository;
    private final UserRepository userRepository;

    public PostingMapper(DormRepository dormRepository, UserRepository userRepository) {
        this.dormRepository = dormRepository;
        this.userRepository = userRepository;
    }

    public PostingDto toDTO(Posting posting) {
        if (posting == null) return null;

        return PostingDto.builder()
                .id(posting.getId())
                .isValid(posting.getIsValid())
                .date(posting.getDate().toString())
                .userId(posting.getUser().getId())
                .sourceDormId(posting.getSourceDorm() != null ? posting.getSourceDorm().getId() : null)
                .targetDormIds(posting.getTargetDorms() != null
                        ? posting.getTargetDorms().stream().map(Dorm::getId).toList()
                        : new ArrayList<>())
                .build();
    }

    public Posting toEntity(PostingDto dto) {
        if (dto == null) return null;

        User user = userRepository.findById(dto.getUserId()).orElse(null);
        Dorm sourceDorm = dormRepository.findById(dto.getSourceDormId()).orElse(null);

        return Posting.builder()
                .id(dto.getId())
                .isValid(dto.getIsValid())
                .user(user)
                .sourceDorm(sourceDorm)
                .targetDorms(dto.getTargetDormIds() != null
                        ? dormRepository.findAllById(dto.getTargetDormIds())
                        : new ArrayList<>())
                .build();
    }

    public Posting toEntity(PostingCreateRequest request) {
        if (request == null) return null;

        User user = userRepository.findById(request.getUserId()).orElse(null);
        Dorm sourceDorm = dormRepository.findById(request.getSourceDormId()).orElse(null);

        return Posting.builder()
                .user(user)
                .sourceDorm(sourceDorm)
                .targetDorms(request.getTargetDormIds() != null
                        ? dormRepository.findAllById(request.getTargetDormIds())
                        : new ArrayList<>())
                .build();
    }
}