package com.sazark.kykbecayis.domain.dto;

import com.sazark.kykbecayis.domain.entities.enums.GenderType;
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
    private GenderType type;
    private String fullAddress;
    private String city;
    private String name;
    private String phoneNumber;

    private List<Long> blockIds;
}
