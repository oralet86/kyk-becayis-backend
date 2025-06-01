package com.sazark.kykbecayis.misc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shaded_package.javax.validation.constraints.NotNull;

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
