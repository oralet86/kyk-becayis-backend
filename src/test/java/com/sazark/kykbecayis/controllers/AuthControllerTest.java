package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.core.enums.Gender;
import com.sazark.kykbecayis.core.filters.JwtAuthFilter;
import com.sazark.kykbecayis.user.JwtService;
import com.sazark.kykbecayis.user.UserService;
import com.sazark.kykbecayis.user.dto.UserCreateRequest;
import com.sazark.kykbecayis.user.dto.UserDto;
import com.sazark.kykbecayis.user.dto.UserLoginRequest;
import com.sazark.kykbecayis.user.dto.UserPatchRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class AuthControllerTest {

    private static final Long USER_ID = 42L;
    private static final String EMAIL = "test@uni.edu.tr";
    private static final String PASSWORD = "secret_pw";
    private static final String JWT = "signed_jwt_token";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthenticationManager authenticationManager;

    private UserDto userDto;

    @BeforeEach
    void init() {
        SecurityContextHolder.clearContext();
        userDto = UserDto.builder()
                .id(USER_ID)
                .firstname("Test")
                .surname("User")
                .email(EMAIL)
                .gender(Gender.MALE)
                .roles(Set.of())
                .build();
    }

    @Test
    void login_validCredentials_setsCookie() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD, List.of()));
        when(userService.getByUserEmail(EMAIL)).thenReturn(userDto);
        when(jwtService.generateToken(EMAIL)).thenReturn(JWT);

        UserLoginRequest body = new UserLoginRequest(EMAIL, PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(JwtAuthFilter.TOKEN_NAME + "=" + JWT)))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(content().string("Login successful"));
    }

    @Test
    void login_badCredentials_401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserLoginRequest(EMAIL, "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingBody_400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_validRequest_201() throws Exception {
        when(userService.create(any(UserCreateRequest.class))).thenReturn(userDto);

        UserCreateRequest req = UserCreateRequest.builder()
                .firstname("Test").surname("User")
                .email("new@uni.edu.tr").phone("1234567890")
                .city("Istanbul").gender(Gender.MALE)
                .currentDormId(10L).password("pw")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/" + USER_ID))
                .andExpect(jsonPath("$.id").value(USER_ID));
    }

    @Test
    void me_authenticated_returnsProfile() throws Exception {
        when(jwtService.validateToken(any())).thenReturn(JwtService.JwtValidationResult.VALID);
        when(jwtService.extractEmail(JWT)).thenReturn(EMAIL);
        when(userService.getByUserEmail(EMAIL)).thenReturn(userDto);

        mockMvc.perform(get("/api/auth/me").cookie(new Cookie(JwtAuthFilter.TOKEN_NAME, JWT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    void me_missingJwt_400() throws Exception {
        when(jwtService.validateToken(any())).thenReturn(JwtService.JwtValidationResult.INVALID);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_clearsCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(JwtAuthFilter.TOKEN_NAME + "=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")));
    }

    @Nested
    class PatchUser {

        private final UserPatchRequest patchReq =
                UserPatchRequest.builder().firstname("NewName").build();

        @Test
        void patch_self_200() throws Exception {
            when(jwtService.validateToken(any())).thenReturn(JwtService.JwtValidationResult.VALID);
            when(jwtService.extractEmail(JWT)).thenReturn(EMAIL);
            when(userService.updateByEmail(EMAIL, patchReq)).thenReturn(userDto);

            mockMvc.perform(patch("/api/auth/me")
                            .cookie(new Cookie(JwtAuthFilter.TOKEN_NAME, JWT))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchReq)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(USER_ID));
        }

        @Test
        void patch_self_notFound_404() throws Exception {
            when(jwtService.validateToken(any())).thenReturn(JwtService.JwtValidationResult.VALID);
            when(jwtService.extractEmail(JWT)).thenReturn(EMAIL);
            when(userService.updateByEmail(EMAIL, patchReq)).thenReturn(null);

            mockMvc.perform(patch("/api/auth/me")
                            .cookie(new Cookie(JwtAuthFilter.TOKEN_NAME, JWT))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchReq)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void patch_unauthenticated_401() throws Exception {
            when(jwtService.validateToken(JWT)).thenReturn(JwtService.JwtValidationResult.INVALID);

            mockMvc.perform(patch("/api/auth/me")
                            .cookie(new Cookie(JwtAuthFilter.TOKEN_NAME, JWT))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchReq)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class DeleteUser {

        @Test
        void deleteExisting_204() throws Exception {
            when(jwtService.validateToken(any())).thenReturn(JwtService.JwtValidationResult.VALID);
            when(jwtService.extractEmail(JWT)).thenReturn(EMAIL);
            when(userService.deleteByEmail(EMAIL)).thenReturn(true);

            mockMvc.perform(delete("/api/auth/me").cookie(new Cookie(JwtAuthFilter.TOKEN_NAME, JWT)))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteMissing_404() throws Exception {
            when(jwtService.validateToken(any())).thenReturn(JwtService.JwtValidationResult.VALID);
            when(jwtService.extractEmail(JWT)).thenReturn(EMAIL);
            when(userService.deleteByEmail(EMAIL)).thenReturn(false);

            mockMvc.perform(delete("/api/auth/me").cookie(new Cookie(JwtAuthFilter.TOKEN_NAME, JWT)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void delete_invalidJwt_401() throws Exception {
            when(jwtService.validateToken(JWT)).thenReturn(JwtService.JwtValidationResult.INVALID);

            mockMvc.perform(delete("/api/auth/me").cookie(new Cookie(JwtAuthFilter.TOKEN_NAME, JWT)))
                    .andExpect(status().isUnauthorized());
        }
    }
}
