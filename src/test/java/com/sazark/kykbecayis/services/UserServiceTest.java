package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.core.enums.Role;
import com.sazark.kykbecayis.core.mapper.UserMapper;
import com.sazark.kykbecayis.housing.dorm.Dorm;
import com.sazark.kykbecayis.housing.dorm.DormRepository;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.user.UserRepository;
import com.sazark.kykbecayis.user.UserService;
import com.sazark.kykbecayis.user.dto.UserCreateRequest;
import com.sazark.kykbecayis.user.dto.UserDto;
import com.sazark.kykbecayis.user.dto.UserPatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private static final Long ID = 123L;
    private static final String EMAIL = "foo@uni.edu.tr";
    private UserRepository userRepository;
    private UserMapper userMapper;
    private DormRepository dormRepository;
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        dormRepository = mock(DormRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, userMapper, dormRepository, passwordEncoder);
    }

    /* create() */

    @Test
    void create_persistsAndReturnsDto() {
        var req = UserCreateRequest.builder().firstname("John").email(EMAIL).build();
        var entity = new User();
        var saved = new User();
        var dto = UserDto.builder().id(ID).firstname("John").build();

        when(userMapper.toEntity(req)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toDTO(saved)).thenReturn(dto);

        var out = userService.create(req);

        assertEquals(dto, out);
        verify(userRepository).save(entity);
    }

    /* getRolesByEmail() */

    @Test
    void getRolesByEmail_nullOrEmpty_throws() {
        assertThrows(NoSuchElementException.class, () -> userService.getRolesByEmail(null));
        assertThrows(NoSuchElementException.class, () -> userService.getRolesByEmail(""));
    }

    @Test
    void getRolesByEmail_notFound_returnsEmptySet() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());
        var roles = userService.getRolesByEmail(EMAIL);
        assertTrue(roles.isEmpty());
    }

    @Test
    void getRolesByEmail_found_returnsRoles() {
        var user = new User();
        user.setRoles(Set.of(Role.USER, Role.ADMIN));
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));

        var roles = userService.getRolesByEmail(EMAIL);
        assertEquals(Set.of(Role.USER, Role.ADMIN), roles);
    }

    /* updateByEmail() */

    @Test
    void updateByEmail_nullOrEmpty_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateByEmail(null, new UserPatchRequest()));
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateByEmail("", new UserPatchRequest()));
    }

    @Test
    void updateByEmail_notFound_throws() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> userService.updateByEmail(EMAIL, new UserPatchRequest()));
    }

    @Test
    void updateByEmail_mergesAllFields() {
        var patch = UserPatchRequest.builder()
                .firstname("A")
                .surname("B")
                .phone("123")
                .city("C")
                .gender(com.sazark.kykbecayis.core.enums.Gender.FEMALE)
                .currentDormId(99L)
                .build();

        var user = new User();
        var dorm = new Dorm();
        dorm.setId(99L);
        user.setFirstname("old");
        user.setSurname("old");
        user.setPhone("old");
        user.setCity("old");
        user.setGender(com.sazark.kykbecayis.core.enums.Gender.MALE);
        user.setRoles(Set.of());

        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));
        when(dormRepository.findById(99L)).thenReturn(Optional.of(dorm));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(UserDto.builder().id(ID).build());

        var out = userService.updateByEmail(EMAIL, patch);

        assertEquals("A", user.getFirstname());
        assertEquals("B", user.getSurname());
        assertEquals("123", user.getPhone());
        assertEquals("C", user.getCity());
        assertEquals(com.sazark.kykbecayis.core.enums.Gender.FEMALE, user.getGender());
        assertEquals(dorm, user.getCurrentDorm());
        assertNotNull(out);
    }

    @Test
    void updateByEmail_ignoresBlankValues() {
        var patch = new UserPatchRequest();  // all null
        var user = new User();
        user.setFirstname("keep");

        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(UserDto.builder().id(ID).build());

        userService.updateByEmail(EMAIL, patch);
        assertEquals("keep", user.getFirstname());
    }

    /* updateById() */

    @Test
    void updateById_mergesAllFields() {
        var patch = UserPatchRequest.builder()
                .firstname("A")
                .surname("B")
                .phone("123")
                .city("C")
                .gender(com.sazark.kykbecayis.core.enums.Gender.FEMALE)
                .currentDormId(99L)
                .build();

        var user = new User();
        var dorm = new Dorm();
        dorm.setId(99L);
        user.setFirstname("old");
        user.setSurname("old");
        user.setPhone("old");
        user.setCity("old");
        user.setGender(com.sazark.kykbecayis.core.enums.Gender.MALE);
        user.setRoles(Set.of());

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(dormRepository.findById(99L)).thenReturn(Optional.of(dorm));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(UserDto.builder().id(ID).build());

        var out = userService.updateById(ID, patch);

        assertEquals("A", user.getFirstname());
        assertEquals("B", user.getSurname());
        assertEquals("123", user.getPhone());
        assertEquals("C", user.getCity());
        assertEquals(com.sazark.kykbecayis.core.enums.Gender.FEMALE, user.getGender());
        assertEquals(dorm, user.getCurrentDorm());
        assertNotNull(out);
    }

    @Test
    void updateById_ignoresBlankValues() {
        var patch = new UserPatchRequest();  // all null
        var user = new User();
        user.setFirstname("keep");

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(UserDto.builder().id(ID).build());

        userService.updateById(ID, patch);
        assertEquals("keep", user.getFirstname());
    }

    @Test
    void updateById_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> userService.updateById(null, new UserPatchRequest()));
    }

    @Test
    void updateById_notFound_throws() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> userService.updateById(ID, new UserPatchRequest()));
    }

    @Test
    void updateById_merges() {
        var patch = UserPatchRequest.builder().firstname("Zed").build();
        var user = new User();
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(UserDto.builder().id(ID).firstname("Zed").build());

        var out = userService.updateById(ID, patch);
        assertEquals("Zed", user.getFirstname());
        assertEquals(ID, out.getId());
    }

    /* deleteById() */

    @Test
    void deleteById_exists_true() {
        when(userRepository.existsById(ID)).thenReturn(true);
        assertTrue(userService.deleteById(ID));
        verify(userRepository).deleteById(ID);
    }

    @Test
    void deleteById_missing_false() {
        when(userRepository.existsById(ID)).thenReturn(false);
        assertFalse(userService.deleteById(ID));
    }

    /* deleteByEmail() */

    @Test
    void deleteByEmail_exists_true() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);
        assertTrue(userService.deleteByEmail(EMAIL));
        verify(userRepository).deleteByEmailIgnoreCase(EMAIL);
    }

    @Test
    void deleteByEmail_missing_false() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        assertFalse(userService.deleteByEmail(EMAIL));
    }

    /* getByUserId() */

    @Test
    void getByUserId_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> userService.getByUserId(null));
    }

    @Test
    void getByUserId_notFound_throws() {
        when(userRepository.findOne((Example<User>) any())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> userService.getByUserId(ID));
    }

    @Test
    void getByUserId_found_returnsDto() {
        var user = new User();
        when(userRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(
                UserDto.builder().id(ID).build()
        );

        var dto = userService.getByUserId(ID);
        assertEquals(ID, dto.getId());
    }

    /* getByUserEmail() */

    @Test
    void getByUserEmail_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> userService.getByUserEmail(null));
    }

    @Test
    void getByUserEmail_notFound_throws() {
        when(userRepository.findOne((Example<User>) any())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> userService.getByUserEmail(EMAIL));
    }

    @Test
    void getByUserEmail_found_returnsDto() {
        var user = new User();
        when(userRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(
                UserDto.builder().id(ID).email(EMAIL).build()
        );

        var dto = userService.getByUserEmail(EMAIL);
        assertEquals(EMAIL, dto.getEmail());
    }


    /* filterUsers() */

    @Test
    void filterUsers_nullOrBlank_throws() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.filterUsers(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.filterUsers("")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.filterUsers("   "))
        );
    }

    @Test
    void filterUsers_nonNumeric_throws() {
        assertThrows(IllegalArgumentException.class, () -> userService.filterUsers("abc"));
    }

    @Test
    void filterUsers_emptyResult_returnsEmptyList() {
        when(userRepository.findAll(any(Specification.class))).thenReturn(List.of());
        var list = userService.filterUsers("7");
        assertTrue(list.isEmpty());
    }

    @Test
    void filterUsers_returnsMappedList() {
        var entity = new User();
        when(userRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
        when(userMapper.toDTO(entity)).thenReturn(UserDto.builder().id(ID).build());

        var list = userService.filterUsers("7");
        assertEquals(1, list.size());
        assertEquals(ID, list.get(0).getId());
    }
}
