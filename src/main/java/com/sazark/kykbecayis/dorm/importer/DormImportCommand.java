package com.sazark.kykbecayis.dorm.importer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class DormImportCommand implements CommandLineRunner {

    private final DormImportService dormImportService;

    @Override
    public void run(String... args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("import-dorms")) {
            try {
                InputStream inputStream;

                if (args.length == 2) {
                    String filePath = args[1];
                    inputStream = new FileInputStream(filePath);
                } else {
                    throw new IllegalArgumentException("You must provide a path to the dorms.json file.");
                }

                dormImportService.importDormsFromJson(inputStream);
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
