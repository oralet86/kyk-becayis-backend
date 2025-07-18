package com.sazark.kykbecayis.user;

import com.sazark.kykbecayis.core.enums.Role;
import com.sazark.kykbecayis.core.mapper.UserMapper;
import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.user.dto.UserCreateRequest;
import com.sazark.kykbecayis.user.dto.UserDto;
import com.sazark.kykbecayis.user.dto.UserPatchRequest;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DormRepository dormRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            DormRepository dormRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.dormRepository = dormRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* Create a new user. */
    public UserDto create(UserCreateRequest userCreateRequest) {
        User user = userMapper.toEntity(userCreateRequest);
        String rawPassword = userCreateRequest.getPassword();
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public Set<Role> getRolesByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new NoSuchElementException("Email is null or empty");
        }
        return userRepository
                .findByEmailIgnoreCase(email)
                .map(User::getRoles)
                .orElse(Set.of());
    }

    /**
     * Update an existing user identified by their id.
     * Only non-null (or non-blank) fields in {@code userPatchRequest} are copied over.
     *
     * @param email            the id of the user to update (must not be blank)
     * @param userPatchRequest dto carrying the fields to change (may contain any subset)
     * @return the updated user as {@link UserPatchRequest}
     * @throws NoSuchElementException   if the user does not exist
     * @throws IllegalArgumentException if {@code id} is null / blank
     */
    @Transactional
    public UserDto updateByEmail(String email, UserPatchRequest userPatchRequest) {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email must not be null or empty");
        }

        User entity = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NoSuchElementException(
                        "User not found with email: " + email));

        /* ---------- merge incoming fields ----------------------------------------------------- */
        if (userPatchRequest.getFirstname() != null && !userPatchRequest.getFirstname().isBlank()) {
            entity.setFirstname(userPatchRequest.getFirstname());
        }
        if (userPatchRequest.getSurname() != null && !userPatchRequest.getSurname().isBlank()) {
            entity.setSurname(userPatchRequest.getSurname());
        }
        if (userPatchRequest.getPhone() != null && !userPatchRequest.getPhone().isBlank()) {
            entity.setPhone(userPatchRequest.getPhone());
        }
        if (userPatchRequest.getCity() != null && !userPatchRequest.getCity().isBlank()) {
            entity.setCity(userPatchRequest.getCity());
        }
        if (userPatchRequest.getGender() != null) {
            entity.setGender(userPatchRequest.getGender());
        }
        if (userPatchRequest.getCurrentDormId() != null) {
            entity.setCurrentDorm(
                    dormRepository.findById(userPatchRequest.getCurrentDormId()).orElseThrow()
            );
        }
        /* -------------------------------------------------------------------------------------- */

        return userMapper.toDTO(userRepository.save(entity));
    }

    /**
     * Update an existing user identified by their id.
     * Only non-null (or non-blank) fields in {@code userPatchRequest} are copied over.
     *
     * @param id               the id of the user to update (must not be blank)
     * @param userPatchRequest dto carrying the fields to change (may contain any subset)
     * @return the updated user as {@link UserPatchRequest}
     * @throws NoSuchElementException   if the user does not exist
     * @throws IllegalArgumentException if {@code id} is null / blank
     */
    @Transactional
    public UserDto updateById(Long id, UserPatchRequest userPatchRequest) {

        if (id == null) {
            throw new IllegalArgumentException("id must not be null or empty");
        }

        User entity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "User not found with id: " + id));

        /* ---------- merge incoming fields ----------------------------------------------------- */
        if (userPatchRequest.getFirstname() != null && !userPatchRequest.getFirstname().isBlank()) {
            entity.setFirstname(userPatchRequest.getFirstname());
        }
        if (userPatchRequest.getSurname() != null && !userPatchRequest.getSurname().isBlank()) {
            entity.setSurname(userPatchRequest.getSurname());
        }
        if (userPatchRequest.getPhone() != null && !userPatchRequest.getPhone().isBlank()) {
            entity.setPhone(userPatchRequest.getPhone());
        }
        if (userPatchRequest.getCity() != null && !userPatchRequest.getCity().isBlank()) {
            entity.setCity(userPatchRequest.getCity());
        }
        if (userPatchRequest.getGender() != null) {
            entity.setGender(userPatchRequest.getGender());
        }
        if (userPatchRequest.getCurrentDormId() != null) {
            entity.setCurrentDorm(
                    dormRepository.findById(userPatchRequest.getCurrentDormId()).orElseThrow()
            );
        }
        /* -------------------------------------------------------------------------------------- */

        return userMapper.toDTO(userRepository.save(entity));
    }

    /* Delete a user identified by their id. */
    public boolean deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    /* Delete a user identified by their email. */
    public boolean deleteByEmail(String email) {
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            return false;
        }
        userRepository.deleteByEmailIgnoreCase(email);
        return true;
    }

    /* Get a user identified by their id. */
    public UserDto getByUserId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("user ID must not be null or empty");
        }

        return userRepository.findOne((root, query, cb) ->
                        cb.equal(root.get("id"), id)
                )
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
    }

    /* Get a user identified by their email. */
    public UserDto getByUserEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("user email must not be null or empty");
        }

        return userRepository.findOne((root, query, cb) ->
                        cb.equal(root.get("email"), email)
                )
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
    }

    /* Filter users by certain parameters. */
    public List<UserDto> filterUsers(String postingId) {
        if (postingId == null || postingId.isBlank()) {
            throw new IllegalArgumentException("postingId is required");
        }

        long postingIdLong;
        try {
            postingIdLong = Long.parseLong(postingId);
        } catch (NumberFormatException e) {
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