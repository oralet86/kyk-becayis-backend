package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.domain.entities.User;
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