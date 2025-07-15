package com.sazark.kykbecayis.core.mapper;

import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.user.dto.UserCreateRequest;
import com.sazark.kykbecayis.user.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    private final DormRepository dormRepository;

    public UserMapper(DormRepository dormRepository) {
        this.dormRepository = dormRepository;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .firstname(dto.getFirstname())
                .surname(dto.getSurname())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .city(dto.getCity())
                .roles(dto.getRoles())
                .currentDorm(dto.getCurrentDormId() != null
                        ? dormRepository.findById(dto.getCurrentDormId()).orElse(null)
                        : null)
                .build();
    }

    public User toEntity(UserCreateRequest dto) {
        if (dto == null) return null;

        return User.builder()
                .firstname(dto.getFirstname())
                .surname(dto.getSurname())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .city(dto.getCity())
                .gender(dto.getGender())
                .currentDorm(dto.getCurrentDormId() != null
                        ? dormRepository.findById(dto.getCurrentDormId()).orElse(null)
                        : null)
                .build();
    }

    public UserDto toDTO(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .city(user.getCity())
                .gender(user.getGender())
                .roles(user.getRoles())
                .isAdmin(user.isAdmin())
                .currentDormId(user.getCurrentDorm() != null
                        ? user.getCurrentDorm().getId()
                        : null)
                .postingIds(user.getPostings() != null
                        ? user.getPostings().stream().map(Posting::getId).toList()
                        : new ArrayList<>())
                .build();
    }
}