package com.sazark.kykbecayis.housing.dorm;

import com.sazark.kykbecayis.core.mapper.DormMapper;
import com.sazark.kykbecayis.housing.dto.DormDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DormService {
    private final DormRepository dormRepository;
    private final DormMapper dormMapper;

    public DormService(DormRepository dormRepository, DormMapper dormMapper) {
        this.dormRepository = dormRepository;
        this.dormMapper = dormMapper;
    }

    public DormDto create(DormDto dormDto) {
        Dorm dorm = dormMapper.toEntity(dormDto);
        Dorm savedDorm = dormRepository.save(dorm);
        return dormMapper.toDTO(savedDorm);
    }

    public DormDto update(Long id, DormDto dormDto) {
        if (!dormRepository.existsById(id)) {
            return null;
        }

        Dorm dorm = dormMapper.toEntity(dormDto);
        dorm.setId(id);
        Dorm savedDorm = dormRepository.save(dorm);
        return dormMapper.toDTO(savedDorm);
    }

    public DormDto findById(Long id) {
        return dormRepository.findById(id)
                .map(dormMapper::toDTO)
                .orElse(null);
    }

    public List<DormDto> findAll() {
        return dormRepository.findAll().stream()
                .map(dormMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean delete(Long id) {
        if (!dormRepository.existsById(id)) {
            return false;
        }
        dormRepository.deleteById(id);
        return true;
    }

    public Instant findLastModifiedTime() {
        Instant lastModifiedTime = dormRepository.findLastModifiedTime();
        return Objects.requireNonNullElse(lastModifiedTime, Instant.EPOCH);
    }
}
