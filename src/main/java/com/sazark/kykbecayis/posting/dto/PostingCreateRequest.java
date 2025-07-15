package com.sazark.kykbecayis.posting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostingCreateRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long sourceDormId;

    private List<Long> targetDormIds;
}
