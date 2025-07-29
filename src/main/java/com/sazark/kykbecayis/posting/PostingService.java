package com.sazark.kykbecayis.posting;

import com.sazark.kykbecayis.core.mapper.PostingMapper;
import com.sazark.kykbecayis.posting.dto.PostingCreateRequest;
import com.sazark.kykbecayis.posting.dto.PostingGetRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public PostingGetRequest create(PostingCreateRequest postingCreateRequest) {
        Posting posting = postingMapper.toEntity(postingCreateRequest);
        Posting savedPosting = postingRepository.save(posting);
        return postingMapper.toDTO(savedPosting);
    }

    public PostingGetRequest update(Long id, PostingGetRequest postingGetRequest) {
        if (!postingRepository.existsById(id)) {
            return null;
        }

        Posting posting = postingMapper.toEntity(postingGetRequest);
        posting.setId(id);
        Posting updatedPosting = postingRepository.save(posting);
        return postingMapper.toDTO(updatedPosting);
    }

    public PostingGetRequest findById(Long id) {
        Posting posting = postingRepository.findById(id).orElse(null);
        return postingMapper.toDTO(posting);
    }

    public List<PostingGetRequest> findAll() {
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

    public List<PostingGetRequest> filterPostings(Long userId,
                                                  Long sourceDormId,
                                                  Long targetDormId) {

        return postingRepository.findAll((root, query, cb)
                        -> getPredicate(userId, sourceDormId, targetDormId, root, cb))
                .stream()
                .map(postingMapper::toDTO)
                .toList();
    }

    public long countPostings(Long userId,
                              Long sourceDormId,
                              Long targetDormId) {

        return postingRepository.count((root, query, cb)
                -> getPredicate(userId, sourceDormId, targetDormId, root, cb));
    }

    private Predicate getPredicate(Long userId, Long sourceDormId, Long targetDormId, Root<Posting> root, CriteriaBuilder cb) {
        List<Predicate> preds = new ArrayList<>();

        if (userId != null) {
            preds.add(cb.equal(root.get("user").get("id"), userId));
        }
        if (sourceDormId != null) {
            preds.add(cb.equal(root.get("sourceDorm").get("id"), sourceDormId));
        }
        if (targetDormId != null) {
            var targets = root.join("targetDorms");
            preds.add(cb.equal(targets.get("id"), targetDormId));
        }
        return cb.and(preds.toArray(new Predicate[0]));
    }
}