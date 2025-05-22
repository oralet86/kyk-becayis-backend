package com.sazark.kykbecayis.dorm;

import com.sazark.kykbecayis.misc.dto.DormDto;
import com.sazark.kykbecayis.misc.enums.GenderType;
import com.sazark.kykbecayis.misc.mapper.DormMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<DormDto> filterDorms(String type, String city, String name) {
        return dormRepository.findAll((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (type != null) {
                        predicates.add(cb.equal(root.get("type"), GenderType.valueOf(type.toUpperCase())));
                    }

                    if (city != null) {
                        predicates.add(cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
                    }

                    if (name != null) {
                        predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                }).stream()
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
