package com.sazark.kykbecayis.misc.dto;

import com.sazark.kykbecayis.misc.enums.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shaded_package.javax.validation.constraints.NotBlank;
import shaded_package.javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockDto {
    private Long id;
    @NotNull
    private GenderType type;
    @NotBlank
    private String fullAddress;
    @NotBlank
    private String city;
    @NotBlank
    private Long dormId;
    @NotBlank
    private String name;
    private String location;
}
