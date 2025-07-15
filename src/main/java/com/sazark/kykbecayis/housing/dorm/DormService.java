package com.sazark.kykbecayis.housing.dorm;

import com.sazark.kykbecayis.core.mapper.DormMapper;
import com.sazark.kykbecayis.housing.dto.DormDto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
                .toList();
    }

    public List<DormDto> filterDorms(String type, String city, String name) {
        return dormRepository.findAll((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    if (type != null) predicates.add(cb.equal(root.get("type"), type));
                    if (city != null) predicates.add(cb.equal(root.get("city"), city));
                    if (name != null) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                    return cb.and(predicates.toArray(new Predicate[0]));
                }).stream()
                .map(dormMapper::toDTO)
                .toList();
    }

    public boolean delete(Long id) {
        if (!dormRepository.existsById(id)) {
            return false;
        }
        dormRepository.deleteById(id);
        return true;
    }
}
