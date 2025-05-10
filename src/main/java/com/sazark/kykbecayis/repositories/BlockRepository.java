package com.sazark.kykbecayis.repositories;

import com.sazark.kykbecayis.domain.entities.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
}
