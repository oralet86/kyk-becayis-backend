package com.sazark.kykbecayis.housing.importer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class DormImportStartupRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DormImportStartupRunner.class);

    private final DormImportService dormImportService;

    @Override
    public void run(String... args) {
        System.out.println("Dorm import running executing..");
        logger.info("Starting dorm import...");
        dormImportService.importDormsFromJson();
        logger.info("Dorm import at startup completed.");
    }
}
