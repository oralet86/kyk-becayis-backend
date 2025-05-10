package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.domain.entities.User;
import com.sazark.kykbecayis.mappers.impl.UserMapper;
import com.sazark.kykbecayis.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void createUser_savesAndReturnsDto() {
        UserDto inputDto = UserDto.builder().firstname("John").build();
        User user = new User();
        User savedUser = new User();
        UserDto outputDto = UserDto.builder().id(1L).firstname("John").build();

        when(userMapper.toEntity(inputDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(outputDto);

        UserDto result = userService.create(inputDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstname());
    }

    @Test
    void findById_returnsMappedDto() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(UserDto.builder().id(1L).build());

        UserDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void delete_existingUser_returnsTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);
        boolean result = userService.delete(1L);
        assertTrue(result);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_nonexistentUser_returnsFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);
        boolean result = userService.delete(999L);
        assertFalse(result);
    }
}
