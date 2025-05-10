package com.sazark.kykbecayis.domain.dto;

import com.sazark.kykbecayis.domain.entities.enums.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockDto {
    private Long id;
    private GenderType type;

    // Addresses
    private String fullAddress;
    private String city;
    private String province;

    // Coordinates
    private Double latitude;
    private Double longitude;

    private Long dormId;
}
