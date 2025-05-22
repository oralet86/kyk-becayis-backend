package com.sazark.kykbecayis.posting;

import com.sazark.kykbecayis.dorm.Dorm;
import com.sazark.kykbecayis.user.User;
import jakarta.persistence.*;
import lombok.*;
import shaded_package.javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Posting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Boolean isValid;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "source_dorm_id")
    private Dorm sourceDorm;

    @ManyToMany
    @JoinTable(
            name = "posting_target_dorms",
            joinColumns = @JoinColumn(name = "posting_id"),
            inverseJoinColumns = @JoinColumn(name = "dorm_id")
    )
    private List<Dorm> targetDorms;

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}
