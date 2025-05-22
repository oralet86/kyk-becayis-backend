package com.sazark.kykbecayis.user;

import com.sazark.kykbecayis.misc.dto.impl.UserBaseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shaded_package.javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserBaseDto>> filterUsers(
            @RequestParam(required = false) String postingId
    ) {
        return ResponseEntity.ok(userService.filterUsers(postingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBaseDto> getUser(@PathVariable Long id) {
        UserBaseDto user = userService.findById(id);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<UserBaseDto>> getUsers() {
        List<UserBaseDto> users = userService.findAll();
        return (users != null) ? ResponseEntity.ok(users) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserBaseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserBaseDto userBaseDto) {
        UserBaseDto updated = userService.update(id, userBaseDto);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}