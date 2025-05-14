package com.sazark.kykbecayis.importer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DormImportCommand implements CommandLineRunner {

    private final DormImportService dormImportService;

    @Override
    public void run(String... args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("import-dorms")) {
            try {
                dormImportService.importDormsFromJson();
                System.out.println("Dorm import completed.");
            } catch (Exception e) {
                System.err.println("Error importing dorms: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
            System.exit(0);
        }
    }
}

