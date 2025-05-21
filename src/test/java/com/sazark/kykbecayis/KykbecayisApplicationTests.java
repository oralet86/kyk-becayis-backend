package com.sazark.kykbecayis;

import com.sazark.kykbecayis.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class KykbecayisApplicationTests {

    @Test
    void contextLoads() {
    }

}
