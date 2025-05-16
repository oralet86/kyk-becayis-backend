package com.sazark.kykbecayis.domain.entities;

import com.sazark.kykbecayis.domain.entities.enums.GenderType;
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

    @NotBlank
    @Column(nullable = false)
    private String location;

    @ManyToOne
    private Dorm dorm;
}
