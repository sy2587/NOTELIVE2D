package com.kumistudy.tag;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.tag.dto.TagRequest;
import com.kumistudy.tag.dto.TagResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public TagService(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TagResponse> list(Long ownerId) {
        return tagRepository.findAllByOwnerIdOrderByNameAsc(ownerId).stream().map(TagResponse::from).toList();
    }

    @Transactional
    public TagResponse create(Long ownerId, TagRequest request) {
        String name = request.name().trim();
        if (tagRepository.existsByOwnerIdAndNameIgnoreCase(ownerId, name)) {
            throw new ApiException(ErrorCode.RESOURCE_CONFLICT, "標籤名稱已存在");
        }
        User owner = userRepository.getReferenceById(ownerId);
        return TagResponse.from(tagRepository.save(new Tag(owner, name)));
    }

    @Transactional
    public void delete(Long ownerId, Long tagId) {
        Tag tag = tagRepository.findByIdAndOwnerId(tagId, ownerId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "找不到標籤"));
        tagRepository.deleteNoteLinks(tagId);
        tagRepository.delete(tag);
    }
}
