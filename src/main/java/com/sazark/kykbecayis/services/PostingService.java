package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.domain.dto.PostingDto;
import com.sazark.kykbecayis.domain.entities.Posting;
import com.sazark.kykbecayis.mappers.impl.PostingMapper;
import com.sazark.kykbecayis.repositories.PostingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostingService {
    private final PostingRepository postingRepository;
    private final PostingMapper postingMapper;

    public PostingService(PostingRepository postingRepository, PostingMapper postingMapper) {
        this.postingRepository = postingRepository;
        this.postingMapper = postingMapper;
    }

    public PostingDto create(PostingDto postingDto) {
        Posting posting = postingMapper.toEntity(postingDto);
        Posting savedPosting = postingRepository.save(posting);
        return postingMapper.toDTO(savedPosting);
    }

    public PostingDto update(Long id, PostingDto postingDto) {
        if (!postingRepository.existsById(id)) {
            return null;
        }

        Posting posting = postingMapper.toEntity(postingDto);
        posting.setId(id);
        Posting updatedPosting = postingRepository.save(posting);
        return postingMapper.toDTO(updatedPosting);
    }

    public PostingDto findById(Long id) {
        Posting posting = postingRepository.findById(id).orElse(null);
        return postingMapper.toDTO(posting);
    }

    public List<PostingDto> findAll() {
        return postingRepository.findAll()
                .stream()
                .map(postingMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean delete(Long id) {
        if (!postingRepository.existsById(id)) {
            return false;
        }
        postingRepository.deleteById(id);
        return true;
    }
}