package com.sazark.kykbecayis.user.dto;

import com.sazark.kykbecayis.core.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserDto {
    @NotNull
    private Long id;
    @NotBlank
    private String firstname;
    @NotBlank
    private String surname;
    @NotBlank
    private String city;
    @NotNull
    private Gender gender;
    @NotNull
    private Long currentDormId;

    private List<Long> postingIds;
}
