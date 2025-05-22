package com.sazark.kykbecayis.dorm.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.dorm.DormJsonDto;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DormJsonReader {

    public List<DormJsonDto> readDormJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("data/dorms.json");
        if (is == null) {
            throw new IllegalStateException("dorms.json not found in resources/data/");
        }
        return mapper.readValue(is, new TypeReference<>() {
        });
    }

    public List<DormJsonDto> readDormJson(InputStream is) throws Exception {
        if (is == null) throw new IllegalStateException("JSON input stream is null.");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(is, new TypeReference<>() {});
    }
}
