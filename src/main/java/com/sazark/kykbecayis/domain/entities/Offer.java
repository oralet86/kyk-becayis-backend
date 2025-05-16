package com.sazark.kykbecayis.domain.entities;

import com.sazark.kykbecayis.domain.entities.enums.OfferStatus;
import jakarta.persistence.*;
import lombok.*;
import shaded_package.javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Posting posting;

    @ManyToOne
    private User sender;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime created;
}
