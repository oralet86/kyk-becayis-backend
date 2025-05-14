package com.sazark.kykbecayis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KykbecayisApplication {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("import-dorms")) {
            System.setProperty("spring.main.web-application-type", "none");
        }
        SpringApplication.run(KykbecayisApplication.class, args);
    }

}
