package com.sazark.kykbecayis.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.domain.entities.User;
import com.sazark.kykbecayis.exception.InvalidEmailException;
import com.sazark.kykbecayis.exception.InvalidUIDException;
import com.sazark.kykbecayis.mappers.impl.UserMapper;
import com.sazark.kykbecayis.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

        try {
            FirebaseAuth.getInstance().getUser(userDto.getFirebaseUID());
        } catch (FirebaseAuthException e) {
            throw new InvalidUIDException("Firebase UID validation unsuccessful");
        }

        // If all checks have been passed
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
}