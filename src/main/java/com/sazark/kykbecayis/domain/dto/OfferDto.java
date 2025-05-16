package com.sazark.kykbecayis.domain.dto;

import com.sazark.kykbecayis.domain.entities.enums.OfferStatus;
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
