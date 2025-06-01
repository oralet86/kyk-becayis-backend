package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.misc.dto.PostingDto;
import com.sazark.kykbecayis.dorm.Dorm;
import com.sazark.kykbecayis.misc.request.PostingCreateRequest;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.posting.PostingService;
import com.sazark.kykbecayis.user.User;
import com.sazark.kykbecayis.misc.mapper.PostingMapper;
import com.sazark.kykbecayis.dorm.DormRepository;
import com.sazark.kykbecayis.posting.PostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostingServiceTest {

    @Mock
    private PostingRepository postingRepository;

    @Mock
    private DormRepository dormRepository;

    @Mock
    private PostingMapper postingMapper;

    @InjectMocks
    private PostingService postingService;

    private User user;
    private Dorm sourceDorm;
    private Dorm targetDorm;
    private PostingDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        sourceDorm = Dorm.builder().id(1L).build();
        targetDorm = Dorm.builder().id(2L).build();

        dto = PostingDto.builder()
                .id(10L)
                .userId(1L)
                .sourceDormId(1L)
                .targetDormIds(List.of(2L))
                .isValid(true)
                .date("2025-05-16")
                .build();

        when(postingMapper.toEntity(any(PostingDto.class))).thenAnswer(invocation -> {
            PostingDto dtoArg = invocation.getArgument(0);
            return Posting.builder()
                    .id(dtoArg.getId())
                    .isValid(dtoArg.getIsValid())
                    .date(LocalDate.parse(dtoArg.getDate()))
                    .user(user)
                    .sourceDorm(sourceDorm)
                    .targetDorms(List.of(targetDorm))
                    .build();
        });

        when(postingMapper.toDTO(any(Posting.class))).thenAnswer(invocation -> {
            Posting p = invocation.getArgument(0);
            return PostingDto.builder()
                    .id(p.getId())
                    .userId(p.getUser().getId())
                    .sourceDormId(p.getSourceDorm().getId())
                    .targetDormIds(p.getTargetDorms().stream().map(Dorm::getId).toList())
                    .isValid(p.getIsValid())
                    .date(p.getDate().toString())
                    .build();
        });

    }

    @Test
    void testCreate() {
        PostingCreateRequest request = PostingCreateRequest.builder()
                .userId(1L)
                .sourceDormId(1L)
                .targetDormIds(List.of(2L))
                .build();

        // Prepare expected Posting entity before save
        Posting postingToSave = Posting.builder()
                .user(user)
                .sourceDorm(sourceDorm)
                .targetDorms(List.of(targetDorm))
                .isValid(true)
                .date(LocalDate.now())
                .build();

        // Prepare saved posting (with ID)
        Posting savedPosting = Posting.builder()
                .id(10L)
                .user(user)
                .sourceDorm(sourceDorm)
                .targetDorms(List.of(targetDorm))
                .isValid(true)
                .date(LocalDate.now())
                .build();

        // Prepare expected DTO result
        PostingDto expectedDto = PostingDto.builder()
                .id(10L)
                .userId(1L)
                .sourceDormId(1L)
                .targetDormIds(List.of(2L))
                .isValid(true)
                .date(savedPosting.getDate().toString())
                .build();

        // Mocks
        when(dormRepository.findAllById(List.of(2L))).thenReturn(List.of(targetDorm));
        when(postingMapper.toEntity(request)).thenReturn(postingToSave);
        when(postingRepository.save(postingToSave)).thenReturn(savedPosting);
        when(postingMapper.toDTO(savedPosting)).thenReturn(expectedDto);

        // Act
        PostingDto result = postingService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(List.of(2L), result.getTargetDormIds());
    }


    @Test
    void testFindById_found() {
        Posting posting = Posting.builder()
                .id(10L)
                .user(user)
                .sourceDorm(sourceDorm)
                .targetDorms(List.of(targetDorm))
                .isValid(true)
                .date(LocalDate.now())
                .build();

        when(postingRepository.findById(10L)).thenReturn(Optional.of(posting));

        PostingDto result = postingService.findById(10L);
        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void testFindById_notFound() {
        when(postingRepository.findById(10L)).thenReturn(Optional.empty());
        assertNull(postingService.findById(10L));
    }

    @Test
    void testDelete_existing() {
        when(postingRepository.existsById(1L)).thenReturn(true);
        boolean result = postingService.delete(1L);
        assertTrue(result);
        verify(postingRepository).deleteById(1L);
    }

    @Test
    void testDelete_notExisting() {
        when(postingRepository.existsById(1L)).thenReturn(false);
        boolean result = postingService.delete(1L);
        assertFalse(result);
    }
}
