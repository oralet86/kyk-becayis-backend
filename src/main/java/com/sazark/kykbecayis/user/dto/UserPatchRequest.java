package com.sazark.kykbecayis.user.dto;

import com.sazark.kykbecayis.core.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPatchRequest {
    private String firstname;
    private String surname;
    private String phone;
    private String city;
    private Gender gender;
    private Long currentDormId;
}
