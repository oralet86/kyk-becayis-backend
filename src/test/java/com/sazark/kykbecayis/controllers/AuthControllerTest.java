package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.auth.FirebaseService;
import com.sazark.kykbecayis.auth.JwtService;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.misc.dto.FirebaseIdTokenDto;
import com.sazark.kykbecayis.misc.dto.user.UserBaseDto;
import com.sazark.kykbecayis.misc.dto.user.UserRegisterDto;
import com.sazark.kykbecayis.misc.enums.Gender;
import com.sazark.kykbecayis.user.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private UserBaseDto userBaseDto;

    @BeforeEach
    public void setup() {
        userBaseDto = new UserBaseDto();
        userBaseDto.setId(1L);
        userBaseDto.setFirebaseUID(UID);
        userBaseDto.setEmail("test@example.com");
    }

    @Test
    public void login_withValidToken_setsCookie() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(VALID_FIREBASE_TOKEN)).thenReturn(UID);
        when(userService.getByFirebaseUID(UID)).thenReturn(userBaseDto);
        when(jwtService.generateToken(UID)).thenReturn(JWT);

        FirebaseIdTokenDto loginRequest = new FirebaseIdTokenDto();
        loginRequest.setFirebaseIdToken(VALID_FIREBASE_TOKEN);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("jwt=" + JWT)))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("HttpOnly")));
    }

    @Test
    public void login_withInvalidToken_returnsUnauthorized() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(INVALID_FIREBASE_TOKEN)).thenThrow(new FirebaseAuthException(
                ErrorCode.UNKNOWN,
                "message",
                null,
                null,
                null));

        FirebaseIdTokenDto loginRequest = new FirebaseIdTokenDto(INVALID_FIREBASE_TOKEN);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_withMissingToken_returnsBadRequest() throws Exception {
        FirebaseIdTokenDto loginRequest = new FirebaseIdTokenDto("");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register_withValidToken_setsCookieAndReturnsCreated() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(VALID_FIREBASE_TOKEN)).thenReturn(UID);
        when(userService.create(any(UserBaseDto.class))).thenReturn(userBaseDto);
        when(jwtService.generateToken(UID)).thenReturn(JWT);

        UserRegisterDto request = new UserRegisterDto();
        request.setFirebaseIdToken(VALID_FIREBASE_TOKEN);
        request.setFirstname("Test");
        request.setSurname("User");
        request.setEmail("test@uni.edu.tr");
        request.setPhone("1234567890");
        request.setCity("Istanbul");
        request.setGender(Gender.MALE);
        request.setCurrentDormId(10L);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("jwt=" + JWT)))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("HttpOnly")));
    }

    @Test
    public void register_withInvalidToken_returnsUnauthorized() throws Exception {
        when(firebaseService.verifyIdTokenAndGetUID(INVALID_FIREBASE_TOKEN)).thenThrow(new FirebaseAuthException(
                ErrorCode.UNKNOWN,
                "message",
                null,
                null,
                null));

        UserRegisterDto request = new UserRegisterDto();
        request.setFirebaseIdToken(INVALID_FIREBASE_TOKEN);
        request.setFirstname("Test");
        request.setSurname("User");
        request.setEmail("test@uni.edu.tr");
        request.setPhone("1234567890");
        request.setCity("Istanbul");
        request.setGender(Gender.MALE);
        request.setCurrentDormId(10L);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logout_shouldClearJwtCookie() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("jwt=;")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("HttpOnly")));
    }

    @Test
    public void expiredJwtToken_shouldBeRejected() throws Exception {
        // Mock an expired token
        when(jwtService.isTokenValid(JWT)).thenReturn(false);

        mockMvc.perform(get("/admin")
                        .cookie(new Cookie("jwt", JWT)))
                .andExpect(status().isUnauthorized());
    }
}
