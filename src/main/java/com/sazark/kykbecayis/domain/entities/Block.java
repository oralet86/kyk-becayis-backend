package com.sazark.kykbecayis.domain.entities;

import com.sazark.kykbecayis.domain.entities.enums.GenderType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GenderType type;

    // Addresses
    private String fullAddress;
    private String city;
    private String province;

    // Coordinates
    private Double latitude;
    private Double longitude;

    @ManyToOne
    private Dorm dorm;
}
