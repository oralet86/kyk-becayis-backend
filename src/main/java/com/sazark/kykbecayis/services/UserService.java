package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.domain.entities.Posting;
import com.sazark.kykbecayis.domain.entities.User;
import com.sazark.kykbecayis.exception.InvalidEmailException;
import com.sazark.kykbecayis.mappers.impl.UserMapper;
import com.sazark.kykbecayis.repositories.UserRepository;
import jakarta.persistence.criteria.Join;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto create(UserDto userDto) {
        if (!userDto.getEmail().toLowerCase().trim().endsWith(".edu.tr")) {
            throw new InvalidEmailException("Email must end with '.edu.tr' to be eligible.");
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserDto update(Long id, UserDto userDto) {
        if (!userRepository.existsById(id)) {
            return null;
        }

        User user = userMapper.toEntity(userDto);
        user.setId(id);
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toDTO(user);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public UserDto getByFirebaseUID(String firebaseUID) {
        if (firebaseUID == null || firebaseUID.isEmpty()) {
            throw new IllegalArgumentException("firebaseUID must not be null or empty");
        }

        return userRepository.findOne((root, query, cb) ->
                        cb.equal(root.get("firebaseUID"), firebaseUID)
                )
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("User not found with firebaseUID: " + firebaseUID));
    }

    public List<UserDto> filterUsers(String postingId) {
        if (postingId == null || postingId.isEmpty()) {
            throw new IllegalArgumentException("postingId must not be null or empty");
        }

        return userRepository.findAll((root, query, cb) -> {
                    // Join User -> Posting
                    Join<User, Posting> postings = root.join("postings");
                    // Filter where posting.id = postingId (converted to Long)
                    Long postingIdLong;
                    try {
                        postingIdLong = Long.valueOf(postingId);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("postingId must be a valid number");
                    }
                    return cb.equal(postings.get("id"), postingIdLong);
                }).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

}