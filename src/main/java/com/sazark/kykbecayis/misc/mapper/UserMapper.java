package com.sazark.kykbecayis.misc.mapper;

import com.sazark.kykbecayis.misc.dto.user.UserNotAuthDto;
import com.sazark.kykbecayis.misc.request.UserCreateRequest;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.misc.dto.user.UserBaseDto;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.dorm.DormRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    private final DormRepository dormRepository;

    public UserMapper(DormRepository dormRepository) {
        this.dormRepository = dormRepository;
    }

    public User toEntity(UserBaseDto dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .firebaseUID(dto.getFirebaseUID())
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

    public UserBaseDto toDTO(User user) {
        if (user == null) return null;

        return UserBaseDto.builder()
                .id(user.getId())
                .firebaseUID(user.getFirebaseUID())
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

    public UserNotAuthDto toNotAuthDTO(User user) {
        if (user == null) return null;

        return UserNotAuthDto.builder()
                .firstname(user.getFirstname())
                .surname(user.getSurname())
                .city(user.getCity())
                .gender(user.getGender())
                .currentDormId(user.getCurrentDorm() != null
                        ? user.getCurrentDorm().getId()
                        : null)
                .postingIds(user.getPostings() != null
                        ? user.getPostings().stream().map(Posting::getId).toList()
                        : new ArrayList<>())
                .build();
    }
}