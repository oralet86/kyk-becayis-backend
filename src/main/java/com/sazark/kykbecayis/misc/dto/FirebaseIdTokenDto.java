package com.sazark.kykbecayis.misc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shaded_package.javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirebaseIdTokenDto {
    @NotBlank
    private String firebaseIdToken;
}
