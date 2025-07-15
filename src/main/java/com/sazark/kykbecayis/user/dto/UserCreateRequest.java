package com.sazark.kykbecayis.user.dto;

import com.sazark.kykbecayis.core.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    @NotBlank
    private String password;
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
