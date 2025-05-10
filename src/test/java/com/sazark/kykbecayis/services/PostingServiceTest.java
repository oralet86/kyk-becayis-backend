package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.domain.dto.PostingDto;
import com.sazark.kykbecayis.domain.entities.Dorm;
import com.sazark.kykbecayis.domain.entities.Posting;
import com.sazark.kykbecayis.domain.entities.User;
import com.sazark.kykbecayis.repositories.DormRepository;
import com.sazark.kykbecayis.repositories.PostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostingServiceTest {

    @Mock
    private PostingRepository postingRepository;

    @Mock
    private DormRepository dormRepository;

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
                .user(user)
                .sourceDorm(sourceDorm)
                .targetDormIds(List.of(2L))
                .build();
    }

    @Test
    void testCreate() {
        when(dormRepository.findAllById(List.of(2L))).thenReturn(List.of(targetDorm));
        when(postingRepository.save(any(Posting.class))).thenAnswer(inv -> {
            Posting p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        PostingDto result = postingService.create(dto);
        assertEquals(10L, result.getId());
        assertEquals(user, result.getUser());
    }

    @Test
    void testFindById_found() {
        Posting posting = Posting.builder()
                .id(10L)
                .user(user)
                .sourceDorm(sourceDorm)
                .targetDorms(List.of(targetDorm))
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
