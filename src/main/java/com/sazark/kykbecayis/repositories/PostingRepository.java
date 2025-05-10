package com.sazark.kykbecayis.repositories;

import com.sazark.kykbecayis.domain.entities.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostingRepository extends JpaRepository<Posting, Long> {
}
