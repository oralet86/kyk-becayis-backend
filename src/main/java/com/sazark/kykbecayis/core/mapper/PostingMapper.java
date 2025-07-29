package com.sazark.kykbecayis.core.mapper;

import com.sazark.kykbecayis.housing.dorm.Dorm;
import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.posting.dto.PostingCreateRequest;
import com.sazark.kykbecayis.posting.dto.PostingGetRequest;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class PostingMapper {

    private final DormRepository dormRepository;
    private final UserRepository userRepository;

    public PostingMapper(DormRepository dormRepository, UserRepository userRepository) {
        this.dormRepository = dormRepository;
        this.userRepository = userRepository;
    }

    /**
     * First letter of each + "***" mask (e.g. "M*** F***").
     */
    private static String maskName(String fullName) {
        if (fullName == null || fullName.isBlank()) return fullName;
        return Arrays.stream(fullName.trim().split("\\s+"))
                .map(p -> p.charAt(0) + "***")
                .collect(Collectors.joining(" "));
    }

    public PostingGetRequest toDTO(Posting posting) {
        if (posting == null) return null;

        // build the full name from firstname + surname
        String rawName = posting.getUser() != null
                ? posting.getUser().getFirstname() + " " + posting.getUser().getSurname()
                : null;
        String censoredName = maskName(rawName);

        return PostingGetRequest.builder()
                .id(posting.getId())
                .isValid(posting.getIsValid())
                .date(posting.getDate().toString())
                .userId(posting.getUser().getId())
                .sourceDormId(posting.getSourceDorm() != null ? posting.getSourceDorm().getId() : null)
                .targetDormIds(posting.getTargetDorms() != null
                        ? posting.getTargetDorms().stream().map(Dorm::getId).toList()
                        : new ArrayList<>())
                .censoredName(censoredName)
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

    public Posting toEntity(PostingGetRequest dto) {
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
}