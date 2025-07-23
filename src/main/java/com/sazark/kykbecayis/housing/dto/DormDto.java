package com.sazark.kykbecayis.housing.dto;

import com.sazark.kykbecayis.core.enums.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DormDto {
    private Long id;
    @NotNull
    private GenderType type;
    @NotBlank
    private String fullAddress;
    @NotBlank
    private String city;
    @NotBlank
    private String name;
    private String phoneNumber;
    private String location;

    private List<BlockDto> blocks;
}
