package com.sazark.kykbecayis.housing.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.housing.dto.DormJsonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Profile("!test")
@Component
public class DormJsonReader {

    private static final Logger log = LoggerFactory.getLogger(DormJsonReader.class);
    private static final String DORMS_JSON = "dorms.json";

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Reads <i>dorms.json</i> from the directory that contains the executable JAR.
     * If the file is missing or cannot be parsed, the method logs the problem and returns
     * an empty list so the application can still start.
     */
    public List<DormJsonDto> readDormJson() {
        try {
            Path jarDir = new ApplicationHome(DormJsonReader.class).getDir().toPath();
            Path jsonPath = jarDir.resolve(DORMS_JSON);

            if (!jsonPath.toFile().exists()) {
                log.error("{} not found next to the JAR (expected at {}) – continuing with empty DB.",
                        DORMS_JSON, jsonPath.toAbsolutePath());
                return Collections.emptyList();
            }

            log.info("Reading {} from {}", DORMS_JSON, jsonPath.toAbsolutePath());
            try (InputStream in = new FileInputStream(jsonPath.toFile())) {
                return mapper.readValue(in, new TypeReference<>() {
                });
            }
        } catch (Exception ex) {
            log.error("Failed to load " + DORMS_JSON + " – continuing with empty DB.", ex);
            return Collections.emptyList();
        }
    }
}
