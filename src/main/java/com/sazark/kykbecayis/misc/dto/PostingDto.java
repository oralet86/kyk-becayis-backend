package com.sazark.kykbecayis.misc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shaded_package.javax.validation.constraints.NotBlank;
import shaded_package.javax.validation.constraints.NotNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostingDto {
    private Long id;

    private String date;

    @NotBlank
    private Boolean isValid;

    @NotNull
    private Long userId;

    @NotNull
    private Long sourceDormId;

    private List<Long> targetDormIds;
}
