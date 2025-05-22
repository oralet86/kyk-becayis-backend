package com.sazark.kykbecayis.block;

import com.sazark.kykbecayis.dorm.Dorm;
import com.sazark.kykbecayis.misc.enums.GenderType;
import jakarta.persistence.*;
import lombok.*;
import shaded_package.javax.validation.constraints.NotBlank;
import shaded_package.javax.validation.constraints.NotNull;

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

    private String location;

    @ManyToOne
    private Dorm dorm;
}
