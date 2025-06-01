package com.sazark.kykbecayis.services;

import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.auth.FirebaseService;
import com.sazark.kykbecayis.user.UserService;
import com.sazark.kykbecayis.misc.dto.user.UserBaseDto;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.misc.mapper.UserMapper;
import com.sazark.kykbecayis.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;
    private FirebaseService firebaseService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        firebaseService = mock(FirebaseService.class);
        userService = new UserService(userRepository, userMapper, firebaseService);
    }

    @Test
    void createUser_savesAndReturnsDto() {
        UserBaseDto inputDto = UserBaseDto.builder().firstname("John").email("test@test.edu.tr").firebaseUID("123").build();
        User user = new User();
        User savedUser = new User();
        UserBaseDto outputDto = UserBaseDto.builder().id(1L).firstname("John").email("test@test.edu.tr").build();

        when(userMapper.toEntity(inputDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(outputDto);

        UserBaseDto result = userService.create(inputDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstname());
    }

    @Test
    void findById_returnsMappedDto() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(UserBaseDto.builder().id(1L).build());

        UserBaseDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void delete_existingUser_returnsTrue() throws Exception {
        Long userId = 1L;
        String firebaseUid = "firebase-uid-123";
        User user = new User();
        user.setId(userId);
        user.setFirebaseUID(firebaseUid);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = userService.delete(userId);

        assertTrue(result);
        verify(firebaseService).deleteUser(firebaseUid);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void delete_nonexistentUser_returnsFalse() throws FirebaseAuthException {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean result = userService.delete(999L);

        assertFalse(result);
        verify(firebaseService, never()).deleteUser(any());
        verify(userRepository, never()).deleteById(any());
    }
}
