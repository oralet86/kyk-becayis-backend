package com.sazark.kykbecayis.misc.dto.user;

import com.sazark.kykbecayis.misc.enums.Gender;
import com.sazark.kykbecayis.misc.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shaded_package.javax.validation.constraints.NotBlank;
import shaded_package.javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBaseDto {
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
    @NotNull
    private Long currentDormId;

    private Set<Role> roles;

    private List<Long> postingIds;
}
