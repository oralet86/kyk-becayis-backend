package com.sazark.kykbecayis.misc.request;

import com.sazark.kykbecayis.misc.enums.Gender;
import lombok.*;
import shaded_package.javax.validation.constraints.NotBlank;
import shaded_package.javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRequest {
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
