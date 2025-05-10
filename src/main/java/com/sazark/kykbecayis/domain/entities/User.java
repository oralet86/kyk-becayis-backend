package com.sazark.kykbecayis.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String surname;
    private String password; // Will be replaced with Firebase Auth in the future

    private String email;
    private String phone;

    @OneToOne
    @JoinColumn(name = "current_dorm_id")
    private Dorm currentDorm;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Posting> postings;
}
