package com.sazark.kykbecayis.domain.entities;

import com.sazark.kykbecayis.domain.entities.enums.GenderType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dorm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GenderType type;

    // Addresses
    private String fullAddress;
    private String city;

    private String name;
    private String phoneNumber;

    @OneToMany(mappedBy = "dorm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blocks;

    @ManyToMany(mappedBy = "targetDorms")
    private List<Posting> postings;
}
