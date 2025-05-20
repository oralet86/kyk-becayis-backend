package com.sazark.kykbecayis.domain.requests;

import com.sazark.kykbecayis.domain.dto.UserDto;
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
