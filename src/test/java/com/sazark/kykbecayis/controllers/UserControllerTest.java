package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.config.TestSecurityConfig;
import com.sazark.kykbecayis.misc.dto.impl.UserBaseDto;
import com.sazark.kykbecayis.user.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Nested
    class FilterUsers {

        @Test
        void shouldReturnFilteredUsers() throws Exception {
            UserBaseDto user = UserBaseDto.builder().id(1L).build();
            Mockito.when(userService.filterUsers("post1")).thenReturn(List.of(user));

            mockMvc.perform(get("/api/users/filter").param("postingId", "post1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        void shouldReturnEmptyListWhenNoMatch() throws Exception {
            Mockito.when(userService.filterUsers("none")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/users/filter").param("postingId", "none"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        }
    }

    @Nested
    class GetUser {

        @Test
        void shouldReturnUserIfExists() throws Exception {
            UserBaseDto user = UserBaseDto.builder().id(10L).build();
            Mockito.when(userService.findById(10L)).thenReturn(user);

            mockMvc.perform(get("/api/users/{id}", 10))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10));
        }

        @Test
        void shouldReturn404IfUserNotFound() throws Exception {
            Mockito.when(userService.findById(99L)).thenReturn(null);

            mockMvc.perform(get("/api/users/{id}", 99))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetUsers {

        @Test
        void shouldReturnListOfUsers() throws Exception {
            UserBaseDto user = UserBaseDto.builder().id(1L).build();
            Mockito.when(userService.findAll()).thenReturn(List.of(user));

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        void shouldReturn404IfUserListIsNull() throws Exception {
            Mockito.when(userService.findAll()).thenReturn(null);

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateUser {

        @Test
        void shouldUpdateUserSuccessfully() throws Exception {
            UserBaseDto input = UserBaseDto.builder().id(2L).build();
            Mockito.when(userService.update(eq(2L), any(UserBaseDto.class))).thenReturn(input);

            mockMvc.perform(put("/api/users/{id}", 2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2));
        }

        @Test
        void shouldReturn404IfUpdateFails() throws Exception {
            UserBaseDto input = UserBaseDto.builder().id(999L).build();
            Mockito.when(userService.update(eq(999L), any(UserBaseDto.class))).thenReturn(null);

            mockMvc.perform(put("/api/users/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteUser {

        @Test
        void shouldDeleteUserSuccessfully() throws Exception {
            Mockito.when(userService.delete(5L)).thenReturn(true);

            mockMvc.perform(delete("/api/users/{id}", 5))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404IfDeleteFails() throws Exception {
            Mockito.when(userService.delete(404L)).thenReturn(false);

            mockMvc.perform(delete("/api/users/{id}", 404))
                    .andExpect(status().isNotFound());
        }
    }
}
