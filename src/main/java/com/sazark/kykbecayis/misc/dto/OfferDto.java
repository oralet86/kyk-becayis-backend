package com.sazark.kykbecayis.misc.dto;

import com.sazark.kykbecayis.misc.enums.OfferStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferDto {
    private Long id;
    private Long postingId;
    private Long senderId;
    private OfferStatus status;
    private LocalDateTime created;
}
