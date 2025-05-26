package com.sazark.kykbecayis.integration;

import com.sazark.kykbecayis.misc.enums.Gender;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthFlowIntegrationIT {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FIREBASE_API_KEY = "AIzaSyBsrhVuAsHnKjVdYTIo9QgUKRBFZT93HfA";
    private static final String BASE_URL = "http://localhost:8080/auth";

    private static final String TEST_EMAIL = "testuser123@x.edu.tr";
    private static final String TEST_PASSWORD = "TestPassword123!";
    private static String firebaseIdToken;

    @Test
    @Order(1)
    public void createFirebaseUser() {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;

        Map<String, Object> body = new HashMap<>();
        body.put("email", TEST_EMAIL);
        body.put("password", TEST_PASSWORD);
        body.put("returnSecureToken", true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        assertEquals(200, response.getStatusCodeValue());
        firebaseIdToken = (String) response.getBody().get("idToken");

        System.out.println(firebaseIdToken);
        assertNotNull(firebaseIdToken);
    }

    @Test
    @Order(2)
    public void registerInBackend() {
        String url = BASE_URL + "/register";

        Map<String, Object> registerBody = new HashMap<>();
        registerBody.put("firebaseIdToken", firebaseIdToken);
        registerBody.put("firstname", "Test");
        registerBody.put("surname", "User");
        registerBody.put("email", TEST_EMAIL);
        registerBody.put("phone", "555-0000");
        registerBody.put("city", "Ankara");
        registerBody.put("gender", Gender.MALE);
        registerBody.put("currentDormId", 1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(registerBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
    }

    @Test
    @Order(3)
    public void loginToBackend() {
        refreshIdToken();

        String url = BASE_URL + "/login";

        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("firebaseIdToken", firebaseIdToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(loginBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
    }

    @Test
    @Order(4)
    public void logoutFromBackend() {
        String url = BASE_URL + "/logout";

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertEquals(200, response.getStatusCodeValue());
    }

    private void refreshIdToken() {
        // Get a new ID token to simulate re-login
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;

        Map<String, Object> body = new HashMap<>();
        body.put("email", TEST_EMAIL);
        body.put("password", TEST_PASSWORD);
        body.put("returnSecureToken", true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        assertEquals(200, response.getStatusCodeValue());
        firebaseIdToken = (String) response.getBody().get("idToken");
    }
}