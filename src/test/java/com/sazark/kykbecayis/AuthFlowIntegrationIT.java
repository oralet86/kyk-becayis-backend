package com.sazark.kykbecayis;

import com.sazark.kykbecayis.core.enums.Gender;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthFlowIntegrationIT {

    private static final String BASE = "http://localhost:8080/api/auth";
    private static final String TEST_EMAIL = "testuser123@x.edu.tr";
    private static final String TEST_PASSWORD = "TestPassword123!";
    /**
     * store the session cookie (jwt) across requests
     */
    private static final AtomicReference<String> COOKIE = new AtomicReference<>();
    private final RestTemplate rest = new RestTemplate();

    @Test
    @Order(1)
    void register() {
        String url = BASE + "/register";

        Map<String, Object> body = Map.of(
                "firstname", "Test",
                "surname", "User",
                "email", TEST_EMAIL,
                "password", TEST_PASSWORD,
                "phone", "555-0000",
                "city", "Ankara",
                "gender", Gender.MALE,
                "currentDormId", 1L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        ResponseEntity<Map> resp =
                rest.postForEntity(url, req, Map.class);

        assertEquals(201, resp.getStatusCodeValue());
        assertTrue(resp.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        // capture the cookie for later (optional)
        COOKIE.set(resp.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
    }

    @Test
    @Order(2)
    void login() {
        String url = BASE + "/login";

        Map<String, Object> body = Map.of(
                "email", TEST_EMAIL,
                "password", TEST_PASSWORD
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp =
                rest.postForEntity(url, req, String.class);

        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getHeaders().containsKey(HttpHeaders.SET_COOKIE));

        // store the jwt cookie for subsequent requests
        COOKIE.set(resp.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
    }

    @Test
    @Order(3)
    void me() {
        String url = BASE + "/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, COOKIE.get());
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<Map> resp =
                rest.exchange(url, HttpMethod.GET, req, Map.class);

        assertEquals(200, resp.getStatusCodeValue());
        Map<?, ?> user = resp.getBody();
        assertEquals(TEST_EMAIL, user.get("email"));
        assertNotNull(user.get("id"));
    }

    @Test
    @Order(4)
    void logout() {
        String url = BASE + "/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, COOKIE.get());
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<String> resp =
                rest.postForEntity(url, req, String.class);

        assertEquals(200, resp.getStatusCodeValue());
        // after logout, cookie should be expired (Max-Age=0)
        String setCookie = resp.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertTrue(setCookie.contains("Max-Age=0"));
    }
}