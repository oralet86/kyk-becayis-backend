package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.user.JwtService;
import com.sazark.kykbecayis.user.UserService;
import com.sazark.kykbecayis.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtService jwtService;

    @BeforeEach
    void setupJwtMock() {
        when(jwtService.validateToken(any())).thenReturn(JwtService.JwtValidationResult.VALID);
        when(jwtService.extractEmail(any())).thenReturn("test@mocked.edu.tr");
    }

    /* /users/filter */
    @Nested
    class FilterUsers {

        @Test
        void returnsFilteredUsers() throws Exception {
            when(userService.filterUsers("post1"))
                    .thenReturn(List.of(UserDto.builder().id(1L).build()));

            mockMvc.perform(get("/api/users/filter").param("postingId", "post1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        void returnsEmptyArrayWhenNoMatch() throws Exception {
            when(userService.filterUsers("none"))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/users/filter").param("postingId", "none"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        }
    }

    /* GET single user */
    @Nested
    class GetUser {

        @Test
        void returnsUserIfExists() throws Exception {
            when(userService.getByUserId(10L))
                    .thenReturn(UserDto.builder().id(10L).build());

            mockMvc.perform(get("/api/users/{id}", 10L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10));
        }

        @Test
        void returns404WhenMissing() throws Exception {
            when(userService.getByUserId(99L)).thenReturn(null);

            mockMvc.perform(get("/api/users/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }
}