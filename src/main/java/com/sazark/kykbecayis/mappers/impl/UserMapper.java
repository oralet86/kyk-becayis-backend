package com.sazark.kykbecayis.mappers.impl;

import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.domain.entities.Posting;
import com.sazark.kykbecayis.domain.entities.User;
import com.sazark.kykbecayis.mappers.Mapper;
import com.sazark.kykbecayis.repositories.DormRepository;
import com.sazark.kykbecayis.repositories.PostingRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper implements Mapper<User, UserDto> {

    private final DormRepository dormRepository;
    private final PostingRepository postingRepository;

    public UserMapper(DormRepository dormRepository, PostingRepository postingRepository) {
        this.dormRepository = dormRepository;
        this.postingRepository = postingRepository;
    }

    @Override
    public UserDto toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .firebaseUID(user.getFirebaseUID())
                .firstname(user.getFirstname())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .currentDormId(user.getCurrentDorm() != null
                        ? user.getCurrentDorm().getId()
                        : null)
                .postingIds(user.getPostings() != null
                        ? user.getPostings().stream().map(Posting::getId).toList()
                        : new ArrayList<>())
                .build();

    }

    @Override
    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return User.builder()
                .id(userDto.getId())
                .firebaseUID(userDto.getFirebaseUID())
                .firstname(userDto.getFirstname())
                .surname(userDto.getSurname())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .currentDorm(userDto.getCurrentDormId() != null
                        ? dormRepository.findById(userDto.getCurrentDormId()).orElse(null)
                        : null)
                .postings(userDto.getPostingIds() != null
                        ? postingRepository.findAllById(userDto.getPostingIds())
                        : new ArrayList<>())
                .build();
    }
}
