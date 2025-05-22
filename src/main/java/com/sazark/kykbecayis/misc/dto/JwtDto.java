package com.sazark.kykbecayis.misc.dto;

import lombok.*;
import shaded_package.javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtDto {
    @NotBlank
    private String jsonWebToken;
}

