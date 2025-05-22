package com.sazark.kykbecayis.importer;

import com.sazark.kykbecayis.domain.dto.json.DormJsonDto;
import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.repositories.DormRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DormImportServiceTest {

    @Autowired
    private DormRepository dormRepository;

    @Autowired
    private DormJsonReader dormJsonReader;

    @Autowired
    private DormImportService dormImportService;

    @Test
    void shouldImportDormsFromTestJson() throws Exception {
        dormRepository.deleteAll();

        // Load test-specific JSON
        InputStream testJson = getClass().getClassLoader().getResourceAsStream("data/dorms.json");
        List<DormJsonDto> dorms = dormJsonReader.readDormJson(testJson);

        for (DormJsonDto dto : dorms) {
            Dorm dorm = invokeMapDorm(dto);
            dormRepository.save(dorm);
        }

        List<Dorm> result = dormRepository.findAll();
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getLocation()).isNotBlank();
    }

    // Helper to access private mapDorm()
    private Dorm invokeMapDorm(DormJsonDto dto) throws Exception {
        Method method = DormImportService.class.getDeclaredMethod("mapDorm", DormJsonDto.class);
        method.setAccessible(true);
        return (Dorm) method.invoke(dormImportService, dto);
    }
}

