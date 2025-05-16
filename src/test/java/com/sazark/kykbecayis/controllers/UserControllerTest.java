package com.sazark.kykbecayis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class UserControllerTestContextConfiguration {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    @DisplayName("POST /api/users - Success")
    void testCreateUser_Success() throws Exception {
        UserDto input = UserDto.builder()
                .firstname("John")
                .surname("Doe")
                .email("john@example.edu.tr")
                .phone("1234567890")
                .currentDormId(null)
                .postingIds(Collections.emptyList())
                .build();

        UserDto created = UserDto.builder()
                .id(1L)
                .firstname("John")
                .surname("Doe")
                .email("john@example.edu.tr")
                .phone("1234567890")
                .currentDormId(null)
                .postingIds(Collections.emptyList())
                .build();

        Mockito.when(userService.create(any(UserDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(content().json(objectMapper.writeValueAsString(created)));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Found")
    void testGetUserById_Found() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .firstname("Jane")
                .surname("Smith")
                .email("jane@example.com")
                .phone("0987654321")
                .currentDormId(2L)
                .postingIds(List.of(10L, 20L))
                .build();

        Mockito.when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Not Found")
    void testGetUserById_NotFound() throws Exception {
        Mockito.when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users - Empty List")
    void testGetAllUsers_Empty() throws Exception {
        Mockito.when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/users - Non-empty List")
    void testGetAllUsers_NonEmpty() throws Exception {
        UserDto u1 = UserDto.builder().id(1L).firstname("A").surname("B").email("a@b.com").phone("111").currentDormId(null).postingIds(Collections.emptyList()).build();
        UserDto u2 = UserDto.builder().id(2L).firstname("C").surname("D").email("c@d.com").phone("222").currentDormId(null).postingIds(Collections.emptyList()).build();
        List<UserDto> users = List.of(u1, u2);
        Mockito.when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Success")
    void testUpdateUser_Success() throws Exception {
        UserDto input = UserDto.builder()
                .firstname("Mike")
                .surname("Tyson")
                .email("mike@example.com")
                .phone("5555555")
                .currentDormId(null)
                .postingIds(Collections.emptyList())
                .build();

        UserDto updated = UserDto.builder()
                .id(5L)
                .firstname("Mike")
                .surname("Tyson")
                .email("mike@example.com")
                .phone("5555555")
                .currentDormId(null)
                .postingIds(Collections.emptyList())
                .build();

        Mockito.when(userService.update(eq(5L), any(UserDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Not Found")
    void testUpdateUser_NotFound() throws Exception {
        UserDto input = UserDto.builder().firstname("X").surname("Y").email("x@y.com").phone("000").currentDormId(null).postingIds(Collections.emptyList()).build();
        Mockito.when(userService.update(eq(100L), any(UserDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Success")
    void testDeleteUser_Success() throws Exception {
        Mockito.when(userService.delete(3L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Not Found")
    void testDeleteUser_NotFound() throws Exception {
        Mockito.when(userService.delete(404L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/404"))
                .andExpect(status().isNotFound());
    }
}