package com.sazark.kykbecayis.housing.dorm;

import com.sazark.kykbecayis.core.enums.GenderType;
import com.sazark.kykbecayis.housing.block.Block;
import com.sazark.kykbecayis.posting.Posting;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderType type;

    @NotBlank
    @Column(nullable = false)
    private String fullAddress;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    private String location;

    @OneToMany(mappedBy = "dorm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blocks;

    @ManyToMany(mappedBy = "targetDorms")
    private List<Posting> postings;
}
