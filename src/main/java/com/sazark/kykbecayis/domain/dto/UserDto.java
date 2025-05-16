package com.sazark.kykbecayis.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shaded_package.javax.validation.constraints.NotBlank;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank
    private String firebaseUID;
    @NotBlank
    private String firstname;
    @NotBlank
    private String surname;
    @NotBlank
    private String email;
    @NotBlank
    private String phone;

    private Long currentDormId;
    private List<Long> postingIds;
}
