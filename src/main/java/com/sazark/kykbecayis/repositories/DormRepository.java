package com.sazark.kykbecayis.repositories;

import com.sazark.kykbecayis.domain.entities.Dorm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DormRepository extends JpaRepository<Dorm, Long> {
}
