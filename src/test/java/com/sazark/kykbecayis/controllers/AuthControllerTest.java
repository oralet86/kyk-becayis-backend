package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.domain.requests.UserCreationRequest;
import com.sazark.kykbecayis.services.FirebaseService;
import com.sazark.kykbecayis.services.JwtService;
import com.sazark.kykbecayis.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FirebaseService firebaseService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String VALID_FIREBASE_TOKEN = "valid_token";
    private final String INVALID_FIREBASE_TOKEN = "invalid_token";
    private final String UID = "firebase_uid";
    private final String JWT = "jwt_token";

    private UserDto userDto;

    @BeforeEach
    public void setup() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirebaseUID(UID);
        userDto.setEmail("test@example.com");
    }

    @Test
    public void login_withValidToken_returnsJwt() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(VALID_FIREBASE_TOKEN)).thenReturn(UID);
        when(userService.getByFirebaseUID(UID)).thenReturn(userDto);
        when(jwtService.generateToken(UID)).thenReturn(JWT);

        AuthController.FirebaseLoginRequest loginRequest = new AuthController.FirebaseLoginRequest(VALID_FIREBASE_TOKEN);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(JWT));
    }

    @Test
    public void login_withInvalidToken_returnsUnauthorized() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(INVALID_FIREBASE_TOKEN)).thenThrow(new FirebaseAuthException(
                ErrorCode.UNKNOWN,
                "message",
                null,
                null,
                null));

        AuthController.FirebaseLoginRequest loginRequest = new AuthController.FirebaseLoginRequest(INVALID_FIREBASE_TOKEN);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_withMissingToken_returnsBadRequest() throws Exception {
        AuthController.FirebaseLoginRequest loginRequest = new AuthController.FirebaseLoginRequest("");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register_withValidToken_returnsCreated() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(VALID_FIREBASE_TOKEN)).thenReturn(UID);
        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        userDto.setFirebaseUID(null);  // simulate not being set
        UserCreationRequest request = new UserCreationRequest();
        request.setFirebaseIdToken(VALID_FIREBASE_TOKEN);
        request.setUserDto(userDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void register_withTokenMismatch_returnsBadRequest() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(VALID_FIREBASE_TOKEN)).thenReturn("other_uid");

        userDto.setFirebaseUID("mismatch_uid");
        UserCreationRequest request = new UserCreationRequest();
        request.setFirebaseIdToken(VALID_FIREBASE_TOKEN);
        request.setUserDto(userDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register_withInvalidToken_returnsUnauthorized() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(INVALID_FIREBASE_TOKEN)).thenThrow(new FirebaseAuthException(
                ErrorCode.UNKNOWN,
                "message",
                null,
                null,
                null));

        userDto.setFirebaseUID(null);
        UserCreationRequest request = new UserCreationRequest();
        request.setFirebaseIdToken(INVALID_FIREBASE_TOKEN);
        request.setUserDto(userDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
