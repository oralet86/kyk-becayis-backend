package com.sazark.kykbecayis.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String firebaseUID;
    private String firstname;
    private String surname;
    private String email;
    private String phone;

    private Long currentDormId;
    private List<Long> postingIds;
}
