package com.sazark.kykbecayis.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazark.kykbecayis.domain.dto.json.DormJsonDto;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DormJsonReader {

    public List<DormJsonDto> readDormJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("dorms.json");
        return mapper.readValue(is, new TypeReference<>() {
        });
    }
}
