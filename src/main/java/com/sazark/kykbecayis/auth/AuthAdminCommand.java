package com.sazark.kykbecayis.auth;

import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.misc.enums.Role;
import com.sazark.kykbecayis.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthAdminCommand implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (args.length == 2 && "make-admin".equals(args[0])) {
            String email = args[1];
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            if (user.getRoles().contains(Role.ADMIN)) {
                System.out.println("User is already an admin.");
            } else {
                user.getRoles().add(Role.ADMIN);
                userRepository.save(user);
                System.out.println("User promoted to admin: " + email);
            }
            System.exit(0);
        }
    }
}
