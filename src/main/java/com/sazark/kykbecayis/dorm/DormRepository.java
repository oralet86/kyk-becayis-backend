package com.sazark.kykbecayis.dorm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DormRepository extends JpaRepository<Dorm, Long>, JpaSpecificationExecutor<Dorm> {
}