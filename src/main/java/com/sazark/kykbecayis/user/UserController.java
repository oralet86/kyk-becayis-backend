package com.sazark.kykbecayis.user;

import com.sazark.kykbecayis.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserDto>> filterUsers(
            @RequestParam(required = false) String postingId
    ) {
        return ResponseEntity.ok(userService.filterUsers(postingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto user = userService.getByUserId(id);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}