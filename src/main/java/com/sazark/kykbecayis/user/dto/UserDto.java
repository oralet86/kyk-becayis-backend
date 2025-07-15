package com.sazark.kykbecayis.user.dto;

import com.sazark.kykbecayis.core.enums.Gender;
import com.sazark.kykbecayis.core.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstname;
    private String surname;
    private String email;
    private String phone;
    private String city;
    private Gender gender;
    private Boolean isAdmin;
    private Long currentDormId;

    private Set<Role> roles;
    private List<Long> postingIds;
}
