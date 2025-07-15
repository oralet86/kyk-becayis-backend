package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.core.mapper.PostingMapper;
import com.sazark.kykbecayis.posting.Posting;
import com.sazark.kykbecayis.posting.PostingRepository;
import com.sazark.kykbecayis.posting.PostingService;
import com.sazark.kykbecayis.posting.dto.PostingCreateRequest;
import com.sazark.kykbecayis.posting.dto.PostingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostingServiceTest {

    private PostingRepository postingRepository;
    private PostingMapper postingMapper;
    private PostingService postingService;

    @BeforeEach
    void setUp() {
        postingRepository = mock(PostingRepository.class);
        postingMapper = mock(PostingMapper.class);
        postingService = new PostingService(postingRepository, postingMapper);
    }

    @Test
    void create_shouldReturnDto() {
        PostingCreateRequest req = new PostingCreateRequest();
        Posting posting = new Posting();
        Posting saved = new Posting();
        PostingDto dto = new PostingDto();

        when(postingMapper.toEntity(req)).thenReturn(posting);
        when(postingRepository.save(posting)).thenReturn(saved);
        when(postingMapper.toDTO(saved)).thenReturn(dto);

        PostingDto result = postingService.create(req);

        assertEquals(dto, result);
    }

    @Test
    void update_shouldReturnNullIfNotExists() {
        Long id = 1L;
        PostingDto dto = new PostingDto();

        when(postingRepository.existsById(id)).thenReturn(false);

        assertNull(postingService.update(id, dto));
    }

    @Test
    void update_shouldUpdateAndReturnDtoIfExists() {
        Long id = 1L;
        PostingDto dto = new PostingDto();
        Posting posting = new Posting();
        Posting updated = new Posting();
        PostingDto resultDto = new PostingDto();

        when(postingRepository.existsById(id)).thenReturn(true);
        when(postingMapper.toEntity(dto)).thenReturn(posting);
        when(postingRepository.save(posting)).thenReturn(updated);
        when(postingMapper.toDTO(updated)).thenReturn(resultDto);

        PostingDto result = postingService.update(id, dto);

        assertEquals(resultDto, result);
        assertEquals(id, posting.getId());
    }

    @Test
    void findById_shouldReturnDtoIfExists() {
        Long id = 1L;
        Posting posting = new Posting();
        PostingDto dto = new PostingDto();

        when(postingRepository.findById(id)).thenReturn(Optional.of(posting));
        when(postingMapper.toDTO(posting)).thenReturn(dto);

        PostingDto result = postingService.findById(id);

        assertEquals(dto, result);
    }

    @Test
    void findById_shouldReturnNullIfNotExists() {
        Long id = 1L;

        when(postingRepository.findById(id)).thenReturn(Optional.empty());
        when(postingMapper.toDTO(null)).thenReturn(null);

        PostingDto result = postingService.findById(id);

        assertNull(result);
    }

    @Test
    void findAll_shouldReturnAllDtos() {
        List<Posting> postList = Arrays.asList(new Posting(), new Posting());
        List<PostingDto> dtoList = Arrays.asList(new PostingDto(), new PostingDto());

        when(postingRepository.findAll()).thenReturn(postList);
        when(postingMapper.toDTO(postList.get(0))).thenReturn(dtoList.get(0));
        when(postingMapper.toDTO(postList.get(1))).thenReturn(dtoList.get(1));

        List<PostingDto> result = postingService.findAll();

        assertEquals(dtoList, result);
    }

    @Test
    void delete_shouldReturnFalseIfNotExists() {
        Long id = 1L;

        when(postingRepository.existsById(id)).thenReturn(false);

        assertFalse(postingService.delete(id));
    }

    @Test
    void delete_shouldReturnTrueIfExists() {
        Long id = 1L;

        when(postingRepository.existsById(id)).thenReturn(true);

        boolean result = postingService.delete(id);

        verify(postingRepository).deleteById(id);
        assertTrue(result);
    }

    @Test
    void filterPostings_shouldDelegateToRepositoryAndReturnDtos() {
        Long userId = 1L, sourceDormId = 2L, targetDormId = 3L;
        List<Posting> postList = Arrays.asList(new Posting(), new Posting());
        List<PostingDto> dtoList = Arrays.asList(new PostingDto(), new PostingDto());

        when(postingRepository.findAll(any(Specification.class))).thenReturn(postList);
        when(postingMapper.toDTO(postList.get(0))).thenReturn(dtoList.get(0));
        when(postingMapper.toDTO(postList.get(1))).thenReturn(dtoList.get(1));

        List<PostingDto> result = postingService.filterPostings(userId, sourceDormId, targetDormId);

        assertEquals(dtoList, result);
    }

    @Test
    void countPostings_shouldDelegateToRepository() {
        Long userId = 1L, sourceDormId = 2L, targetDormId = 3L;
        long count = 42L;

        when(postingRepository.count(any(Specification.class))).thenReturn(count);

        long result = postingService.countPostings(userId, sourceDormId, targetDormId);

        assertEquals(count, result);
    }
}