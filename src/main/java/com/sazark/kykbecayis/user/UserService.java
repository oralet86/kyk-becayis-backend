package com.sazark.kykbecayis.user;

import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.auth.FirebaseService;
import com.sazark.kykbecayis.misc.mapper.UserMapper;
import com.sazark.kykbecayis.misc.dto.user.UserBaseDto;
import com.sazark.kykbecayis.posting.Posting;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FirebaseService firebaseService;

    public UserService(UserRepository userRepository, UserMapper userMapper, FirebaseService firebaseService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.firebaseService = firebaseService;
    }

    public UserBaseDto create(UserBaseDto userBaseDto) {
        User user = userMapper.toEntity(userBaseDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserBaseDto update(Long id, UserBaseDto userBaseDto) {
        if (!userRepository.existsById(id)) {
            return null;
        }

        User user = userMapper.toEntity(userBaseDto);
        user.setId(id);
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    public UserBaseDto findById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toDTO(user);
    }

    public List<UserBaseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        // Get the users firebase uid
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        String userUID = user.getFirebaseUID();

        try {
            firebaseService.deleteUser(userUID);
        } catch (FirebaseAuthException e) {
            System.out.println(e.getMessage());
        }

        userRepository.deleteById(id);
        return true;
    }

    public UserBaseDto getByFirebaseUID(String firebaseUID) {
        if (firebaseUID == null || firebaseUID.isEmpty()) {
            throw new IllegalArgumentException("firebaseUID must not be null or empty");
        }

        return userRepository.findOne((root, query, cb) ->
                        cb.equal(root.get("firebaseUID"), firebaseUID)
                )
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("User not found with firebaseUID: " + firebaseUID));
    }

    public List<UserBaseDto> filterUsers(String postingId) {
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