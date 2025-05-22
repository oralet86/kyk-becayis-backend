package com.sazark.kykbecayis.user;

import com.sazark.kykbecayis.misc.mapper.UserMapper;
import com.sazark.kykbecayis.misc.dto.UserDto;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.exception.InvalidEmailException;
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
        if (postingId == null || postingId.isBlank()) {
            throw new IllegalArgumentException("postingId is required");
        }

        long postingIdLong;
        try {
            postingIdLong = Long.parseLong(postingId);
        } catch (NumberFormatException e) {
            // Could also return empty list instead of exception if preferred
            throw new IllegalArgumentException("postingId must be a valid numeric ID");
        }

        return userRepository.findAll((root, query, cb) -> {
                    Join<User, Posting> postings = root.join("postings");
                    return cb.equal(postings.get("id"), postingIdLong);
                }).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}