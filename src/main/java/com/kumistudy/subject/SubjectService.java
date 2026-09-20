package com.kumistudy.subject;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.subject.dto.SubjectRequest;
import com.kumistudy.subject.dto.SubjectResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectService {

    private static final String DEFAULT_COLOR = "#6F9987";
    private static final String DEFAULT_ICON = "book-open";

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public SubjectService(SubjectRepository subjectRepository, UserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> list(Long ownerId) {
        return subjectRepository.findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(ownerId)
                .stream()
                .map(SubjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse get(Long ownerId, Long subjectId) {
        return SubjectResponse.from(requireOwnedSubject(ownerId, subjectId));
    }

    @Transactional
    public SubjectResponse create(Long ownerId, SubjectRequest request) {
        String name = request.name().trim();
        ensureUniqueName(ownerId, name, null);
        User owner = userRepository.getReferenceById(ownerId);
        Subject subject = new Subject(
                owner,
                name,
                valueOrDefault(request.color(), DEFAULT_COLOR),
                valueOrDefault(request.icon(), DEFAULT_ICON),
                trimToNull(request.studyGoal()),
                progressOrDefault(request.progress()),
                trimToNull(request.description())
        );
        return SubjectResponse.from(subjectRepository.save(subject));
    }

    @Transactional
    public SubjectResponse update(Long ownerId, Long subjectId, SubjectRequest request) {
        Subject subject = requireOwnedSubject(ownerId, subjectId);
        String name = request.name().trim();
        ensureUniqueName(ownerId, name, subjectId);
        subject.update(
                name,
                valueOrDefault(request.color(), DEFAULT_COLOR),
                valueOrDefault(request.icon(), DEFAULT_ICON),
                trimToNull(request.studyGoal()),
                progressOrDefault(request.progress()),
                trimToNull(request.description())
        );
        return SubjectResponse.from(subject);
    }

    @Transactional
    public void delete(Long ownerId, Long subjectId) {
        requireOwnedSubject(ownerId, subjectId).delete();
    }

    private Subject requireOwnedSubject(Long ownerId, Long subjectId) {
        return subjectRepository.findByIdAndOwnerIdAndDeletedAtIsNull(subjectId, ownerId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "找不到指定科目"));
    }

    private void ensureUniqueName(Long ownerId, String name, Long excludedId) {
        boolean exists = excludedId == null
                ? subjectRepository.existsByOwnerIdAndNameIgnoreCaseAndDeletedAtIsNull(ownerId, name)
                : subjectRepository.existsByOwnerIdAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(ownerId, name, excludedId);
        if (exists) {
            throw new ApiException(ErrorCode.RESOURCE_CONFLICT, "科目名稱已存在");
        }
    }

    private String valueOrDefault(String value, String defaultValue) {
        String normalized = trimToNull(value);
        return normalized == null ? defaultValue : normalized;
    }

    private int progressOrDefault(Integer progress) {
        return progress == null ? 0 : progress;
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
