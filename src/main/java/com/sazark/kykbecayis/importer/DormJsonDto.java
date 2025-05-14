package com.sazark.kykbecayis.importer;

import lombok.Data;
import java.util.List;

@Data
public class DormJsonDto {
    private String Name;
    private String Type;
    private String Phone;
    private String Address;
    private String City;
    private List<BlockJsonDto> Blocks;
}

