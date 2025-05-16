package com.sazark.kykbecayis.domain.dto.json;

import lombok.Data;
import java.util.List;

@Data
public class DormJsonDto {
    private String Name;
    private String Type;
    private String Phone;
    private String Address;
    private String City;
    private String Location;
    private List<BlockJsonDto> Blocks;
}

