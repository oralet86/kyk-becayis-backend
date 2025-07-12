package com.sazark.kykbecayis.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @GetMapping("/api/admin")
    public ResponseEntity<String> adminPanel() {
        return ResponseEntity.ok("Welcome to the admin panel");
    }
}
