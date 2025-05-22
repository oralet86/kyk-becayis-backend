package com.sazark.kykbecayis.misc.mapper;

import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.misc.dto.impl.UserBaseDto;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.misc.Mapper;
import com.sazark.kykbecayis.dorm.DormRepository;
import com.sazark.kykbecayis.posting.PostingRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper implements Mapper<User, UserBaseDto> {

    private final DormRepository dormRepository;
    private final PostingRepository postingRepository;

    public UserMapper(DormRepository dormRepository, PostingRepository postingRepository) {
        this.dormRepository = dormRepository;
        this.postingRepository = postingRepository;
    }

    @Override
    public UserBaseDto toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserBaseDto.builder()
                .id(user.getId())
                .firebaseUID(user.getFirebaseUID())
                .firstname(user.getFirstname())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .city(user.getCity())
                .gender(user.getGender())
                .isAdmin(user.isAdmin())
                .currentDormId(user.getCurrentDorm() != null
                        ? user.getCurrentDorm().getId()
                        : null)
                .postingIds(user.getPostings() != null
                        ? user.getPostings().stream().map(Posting::getId).toList()
                        : new ArrayList<>())
                .build();
    }

    @Override
    public User toEntity(UserBaseDto userBaseDto) {
        if (userBaseDto == null) {
            return null;
        }

        return User.builder()
                .id(userBaseDto.getId())
                .firebaseUID(userBaseDto.getFirebaseUID())
                .firstname(userBaseDto.getFirstname())
                .surname(userBaseDto.getSurname())
                .email(userBaseDto.getEmail())
                .phone(userBaseDto.getPhone())
                .gender(userBaseDto.getGender())
                .city(userBaseDto.getCity())
                .currentDorm(userBaseDto.getCurrentDormId() != null
                        ? dormRepository.findById(userBaseDto.getCurrentDormId()).orElse(null)
                        : null)
                .postings(userBaseDto.getPostingIds() != null
                        ? postingRepository.findAllById(userBaseDto.getPostingIds())
                        : new ArrayList<>())
                .build();
    }
}
