package com.sazark.kykbecayis.misc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shaded_package.javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferCreateRequest {
    @NotBlank
    private Long postingId;

    @NotBlank
    private Long senderId;
}
