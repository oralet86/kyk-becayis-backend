package com.sazark.kykbecayis.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.domain.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_success() throws Exception {
        UserDto userDto = UserDto.builder()
                .firebaseUID("uid-xyz")
                .firstname("Alice")
                .surname("Smith")
                .email("alice@example.edu.tr")
                .phone("555-1234")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firebaseUID").value("uid-xyz"))
                .andExpect(jsonPath("$.firstname").value("Alice"));
    }

    @Test
    void createUser_blankFields_shouldFail() throws Exception {
        UserDto userDto = UserDto.builder()
                .firebaseUID("")
                .firstname("")
                .surname("")
                .email("")
                .phone("")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_invalidEmail_shouldFail() throws Exception {
        UserDto userDto = UserDto.builder()
                .firebaseUID("uid-abc")
                .firstname("Bob")
                .surname("Brown")
                .email("bob@example.com")
                .phone("1234567890")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_success() throws Exception {
        UserDto created = createAndReturnUser("uid-get", "Charlie", "Clark", "charlie@mail.edu.tr", "555-0000");

        mockMvc.perform(get("/api/users/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("charlie@mail.edu.tr"));
    }

    @Test
    void getUser_notFound() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_success() throws Exception {
        createAndReturnUser("uid-list", "Dora", "Dane", "dora@mail.edu.tr", "444-8888");

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void updateUser_success() throws Exception {
        UserDto user = createAndReturnUser("uid-update", "Eve", "Evans", "eve@mail.edu.tr", "111-2222");

        UserDto updatedDto = UserDto.builder()
                .id(user.getId())
                .firebaseUID(user.getFirebaseUID())
                .firstname("Eve Updated")
                .surname("Evans")
                .email("eve@mail.edu.tr")
                .phone("111-2222")
                .build();

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Eve Updated"));
    }

    @Test
    void updateUser_notFound() throws Exception {
        UserDto userDto = UserDto.builder()
                .firebaseUID("ghost")
                .firstname("Ghost")
                .surname("User")
                .email("ghost@mail.edu.tr")
                .phone("000-0000")
                .build();

        mockMvc.perform(put("/api/users/123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_success() throws Exception {
        UserDto user = createAndReturnUser("uid-delete", "Frank", "Ford", "frank@mail.edu.tr", "222-3333");

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        mockMvc.perform(delete("/api/users/98765"))
                .andExpect(status().isNotFound());
    }

    private UserDto createAndReturnUser(String firebaseUID, String firstname, String surname, String email, String phone) throws Exception {
        UserDto dto = UserDto.builder()
                .firebaseUID(firebaseUID)
                .firstname(firstname)
                .surname(surname)
                .email(email)
                .phone(phone)
                .build();

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, UserDto.class);
    }
}
