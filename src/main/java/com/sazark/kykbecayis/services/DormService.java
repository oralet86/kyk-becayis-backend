package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.domain.dto.DormDto;
import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.mappers.impl.DormMapper;
import com.sazark.kykbecayis.repositories.DormRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Dorm dorm = dormRepository.findById(id).orElse(null);
        return dormMapper.toDTO(dorm);
    }

    public List<DormDto> findAll() {
        return dormRepository.findAll()
                .stream()
                .map(dormMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean delete(Long id) {
        if(!dormRepository.existsById(id)) {
            return false;
        }
        dormRepository.deleteById(id);
        return true;
    }
}
