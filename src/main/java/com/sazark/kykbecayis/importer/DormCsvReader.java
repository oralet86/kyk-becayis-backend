package com.sazark.kykbecayis.importer;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class DormCsvReader {

    private static final String CSV_PATH = "data/yurt.csv";

    public List<String[]> readDormCsv() throws Exception {
        List<String[]> rows = new ArrayList<>();

        InputStream is = getClass().getClassLoader().getResourceAsStream(CSV_PATH);
        if (is == null) {
            throw new IllegalStateException("CSV file not found in classpath: " + CSV_PATH);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    line = line.replace("\uFEFF", ""); // Remove BOM
                    firstLine = false;
                }
                String[] columns = line.split(",\\s*");
                rows.add(columns);
            }
        }

        return rows;
    }
}
