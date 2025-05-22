package com.sazark.kykbecayis.offer;

import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.misc.enums.OfferStatus;
import com.sazark.kykbecayis.user.User;
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
