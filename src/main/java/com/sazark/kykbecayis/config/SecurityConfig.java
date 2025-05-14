package com.sazark.kykbecayis.config;

import com.sazark.kykbecayis.firebase.FirebaseAuthFilter;
import com.sazark.kykbecayis.repositories.UserRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<FirebaseAuthFilter> firebaseAuthFilter(UserRepository userRepository) {
        FilterRegistrationBean<FirebaseAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FirebaseAuthFilter(userRepository));
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
