package com.sazark.kykbecayis.misc.dto.impl;

import com.sazark.kykbecayis.misc.dto.UserDto;
import com.sazark.kykbecayis.misc.enums.Gender;
import lombok.*;
import shaded_package.javax.validation.constraints.NotBlank;
import shaded_package.javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDto implements UserDto {
    @NotBlank
    private String firebaseIdToken;
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
    private Long currentDormId;
}
