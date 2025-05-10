package com.sazark.kykbecayis.domain.dto;

import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostingDto {
    private Long id;
    private User user;
    private Dorm sourceDorm;
    private List<Long> targetDormIds;
}
