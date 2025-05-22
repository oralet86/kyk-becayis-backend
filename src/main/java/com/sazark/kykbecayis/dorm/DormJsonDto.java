package com.sazark.kykbecayis.dorm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sazark.kykbecayis.block.BlockJsonDto;
import lombok.Data;
import java.util.List;

@Data
public class DormJsonDto {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("Blocks")
    private List<BlockJsonDto> blocks;
}


