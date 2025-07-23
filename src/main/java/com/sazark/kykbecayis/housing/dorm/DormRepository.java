package com.sazark.kykbecayis.housing.dorm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface DormRepository extends JpaRepository<Dorm, Long>, JpaSpecificationExecutor<Dorm> {
    Optional<Dorm> findByNameAndCity(String name, String city);

    @Query("select max(d.updatedAt) from Dorm d")
    Instant findLastModifiedTime();
}