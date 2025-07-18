package com.sazark.kykbecayis.core.mapper;

import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.user.dto.UserCreateRequest;
import com.sazark.kykbecayis.user.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    private final DormRepository dormRepository;

    public UserMapper(DormRepository dormRepository) {
        this.dormRepository = dormRepository;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setFirstname(dto.getFirstname());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setCity(dto.getCity());
        user.setRoles(dto.getRoles());

        if (dto.getCurrentDormId() != null) {
            user.setCurrentDorm(dormRepository.findById(dto.getCurrentDormId()).orElse(null));
        }

        return user;
    }

    public User toEntity(UserCreateRequest dto) {
        if (dto == null) return null;

        User user = new User();
        user.setFirstname(dto.getFirstname());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setCity(dto.getCity());
        user.setGender(dto.getGender());

        if (dto.getCurrentDormId() != null) {
            user.setCurrentDorm(dormRepository.findById(dto.getCurrentDormId()).orElse(null));
        }

        return user;
    }

    public UserDto toDTO(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setCity(user.getCity());
        dto.setGender(user.getGender());
        dto.setRoles(user.getRoles());
        dto.setIsAdmin(user.isAdmin());

        if (user.getCurrentDorm() != null) {
            dto.setCurrentDormId(user.getCurrentDorm().getId());
        }

        List<Long> postingIds = new ArrayList<>();
        if (user.getPostings() != null) {
            for (Posting posting : user.getPostings()) {
                postingIds.add(posting.getId());
            }
        }
        dto.setPostingIds(postingIds);

        return dto;
    }
}