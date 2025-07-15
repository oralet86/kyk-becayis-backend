package com.sazark.kykbecayis.housing.dorm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sazark.kykbecayis.housing.block.BlockJsonDto;
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


