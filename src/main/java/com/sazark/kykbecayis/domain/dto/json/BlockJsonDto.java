package com.sazark.kykbecayis.domain.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BlockJsonDto {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("City")
    private String city;
}
