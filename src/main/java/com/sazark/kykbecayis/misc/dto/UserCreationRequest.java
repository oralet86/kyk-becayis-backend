package com.sazark.kykbecayis.misc.dto;

import lombok.Getter;
import lombok.Setter;
import shaded_package.javax.validation.Valid;
import shaded_package.javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserCreationRequest {
    @Valid
    private UserDto userDto;
    @NotBlank
    private String firebaseIdToken;
}
