package com.sazark.kykbecayis.housing.block;

import com.sazark.kykbecayis.housing.dorm.Dorm;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BlockEntityListener {
    @PrePersist
    @PreUpdate
    public void onCreateOrUpdate(final Block block) {
        Dorm parentDorm = block.getDorm();
        if (parentDorm != null) {
            parentDorm.setUpdatedAt(Instant.now());
        }
    }
}
