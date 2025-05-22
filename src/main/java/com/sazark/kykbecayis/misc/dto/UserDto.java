package com.sazark.kykbecayis.misc.dto;

import com.sazark.kykbecayis.misc.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shaded_package.javax.validation.constraints.NotBlank;
import shaded_package.javax.validation.constraints.NotNull;

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
    @NotBlank
    private String city;
    @NotNull
    private Gender gender;
    @NotNull
    private Boolean isAdmin;

    private Long currentDormId;
    private List<Long> postingIds;
}
