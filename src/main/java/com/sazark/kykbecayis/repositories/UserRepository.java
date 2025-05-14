package com.sazark.kykbecayis.repositories;

import com.sazark.kykbecayis.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirebaseUID(String firebaseUID);
}
