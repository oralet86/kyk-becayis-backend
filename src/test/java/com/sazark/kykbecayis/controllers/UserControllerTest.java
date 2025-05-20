package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.domain.requests.UserCreationRequest;
import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.services.FirebaseService;
import com.sazark.kykbecayis.services.JwtService;
import com.sazark.kykbecayis.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private FirebaseService firebaseService;

    @MockBean
    private JwtService jwtService;

    @Nested
    @DisplayName("POST /api/users/register")
    class RegisterTests {

        @Test
        @DisplayName("should register user successfully and return 201")
        void registerSuccess() throws Exception {
            String firebaseToken = "valid-firebase-token";
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .firebaseUID("firebase-uid-123")
                    .build();
            UserCreationRequest request = new UserCreationRequest();
            request.setFirebaseIdToken(firebaseToken);
            request.setUserDto(UserDto.builder().build());

            Mockito.when(firebaseService.verifyIdTokenAndGetUID(firebaseToken))
                    .thenReturn("firebase-uid-123");
            Mockito.when(userService.create(any(UserDto.class)))
                    .thenReturn(userDto);

            mockMvc.perform(post("/api/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/users/1"))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firebaseUID").value("firebase-uid-123"));
        }

        @Test
        @DisplayName("should return 400 if Firebase UID mismatch")
        void registerFirebaseUidMismatch() throws Exception {
            UserCreationRequest request = new UserCreationRequest();
            request.setFirebaseIdToken("token");
            UserDto userDto = UserDto.builder()
                    .firebaseUID("different-uid")
                    .build();
            request.setUserDto(userDto);

            Mockito.when(firebaseService.verifyIdTokenAndGetUID("token"))
                    .thenReturn("firebase-uid-123");

            mockMvc.perform(post("/api/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 401 if Firebase token is invalid")
        void registerFirebaseAuthException() throws Exception {
            UserCreationRequest request = new UserCreationRequest();
            request.setFirebaseIdToken("bad-token");
            request.setUserDto(UserDto.builder().build());

            Mockito.when(firebaseService.verifyIdTokenAndGetUID("bad-token"))
                    .thenThrow(new FirebaseAuthException(
                            ErrorCode.UNKNOWN,
                            "Invalid token",
                            null,
                            null,
                            null
                    ));

            mockMvc.perform(post("/api/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/users/login")
    class LoginTests {

        @Test
        @DisplayName("should login successfully and return JWT token")
        void loginSuccess() throws Exception {
            String firebaseToken = "valid-token";
            String uid = "firebase-uid-123";
            UserDto user = UserDto.builder().firebaseUID(uid).build();
            String jwtToken = "jwt-token";

            Mockito.when(firebaseService.verifyIdTokenAndGetUID(firebaseToken)).thenReturn(uid);
            Mockito.when(userService.getByFirebaseUID(uid)).thenReturn(user);
            Mockito.when(jwtService.generateToken(uid)).thenReturn(jwtToken);

            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(firebaseToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(jwtToken));
        }

        @Test
        @DisplayName("should return 401 if Firebase token invalid")
        void loginInvalidFirebaseToken() throws Exception {
            Mockito.when(firebaseService.verifyIdTokenAndGetUID("bad-token"))
                    .thenThrow(new FirebaseAuthException(
                            ErrorCode.UNKNOWN,
                            "Invalid token",
                            null,
                            null,
                            null
                    ));

            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("bad-token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Invalid Firebase token"));
        }

        @Test
        @DisplayName("should return 401 if user not found")
        void loginUserNotFound() throws Exception {
            String token = "valid-token";
            String uid = "uid-123";

            Mockito.when(firebaseService.verifyIdTokenAndGetUID(token)).thenReturn(uid);
            Mockito.when(userService.getByFirebaseUID(uid)).thenReturn(null);

            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(token))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("User not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/users/filter")
    class FilterUsersTests {

        @Test
        @DisplayName("should return filtered users")
        void filterUsersSuccess() throws Exception {
            UserDto user = UserDto.builder().id(1L).build();
            List<UserDto> users = List.of(user);

            Mockito.when(userService.filterUsers("post123")).thenReturn(users);

            mockMvc.perform(get("/api/users/filter")
                            .param("postingId", "post123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        @DisplayName("should return empty list when no users match filter")
        void filterUsersEmpty() throws Exception {
            Mockito.when(userService.filterUsers("unknown")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/users/filter")
                            .param("postingId", "unknown"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserTests {

        @Test
        @DisplayName("should return user by id")
        void getUserSuccess() throws Exception {
            UserDto user = UserDto.builder().id(5L).build();

            Mockito.when(userService.findById(5L)).thenReturn(user);

            mockMvc.perform(get("/api/users/{id}", 5L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void getUserNotFound() throws Exception {
            Mockito.when(userService.findById(42L)).thenReturn(null);

            mockMvc.perform(get("/api/users/{id}", 42L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetUsersTests {

        @Test
        @DisplayName("should return list of users")
        void getUsersSuccess() throws Exception {
            UserDto user = UserDto.builder().id(1L).build();
            List<UserDto> users = List.of(user);

            Mockito.when(userService.findAll()).thenReturn(users);

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        @DisplayName("should return 404 if no users found")
        void getUsersNotFound() throws Exception {
            Mockito.when(userService.findAll()).thenReturn(null);

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateUserTests {

        @Test
        @DisplayName("should update user successfully")
        void updateUserSuccess() throws Exception {
            UserDto updatedUser = UserDto.builder().id(1L).build();

            Mockito.when(userService.update(eq(1L), any(UserDto.class))).thenReturn(updatedUser);

            mockMvc.perform(put("/api/users/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("should return 404 when updating non-existing user")
        void updateUserNotFound() throws Exception {
            UserDto userToUpdate = UserDto.builder().id(999L).build();

            Mockito.when(userService.update(eq(999L), any(UserDto.class))).thenReturn(null);

            mockMvc.perform(put("/api/users/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userToUpdate)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteUserTests {

        @Test
        @DisplayName("should delete user successfully")
        void deleteUserSuccess() throws Exception {
            Mockito.when(userService.delete(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/users/{id}", 1L))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 when deleting non-existing user")
        void deleteUserNotFound() throws Exception {
            Mockito.when(userService.delete(999L)).thenReturn(false);

            mockMvc.perform(delete("/api/users/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }
}
