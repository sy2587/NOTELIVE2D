package com.kumistudy.note;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.folder.Folder;
import com.kumistudy.folder.FolderRepository;
import com.kumistudy.note.dto.NoteRequest;
import com.kumistudy.note.dto.NotePageResponse;
import com.kumistudy.note.dto.NoteResponse;
import com.kumistudy.tag.Tag;
import com.kumistudy.tag.TagRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final FolderRepository folderRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository, TagRepository tagRepository, FolderRepository folderRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.folderRepository = folderRepository;
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> list(Long ownerId) {
        return noteRepository.findAllByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(ownerId)
                .stream().map(NoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public NoteResponse get(Long ownerId, Long id) { return NoteResponse.from(requireOwned(ownerId, id)); }

    @Transactional(readOnly = true)
    public NotePageResponse search(Long ownerId, String query, Long folderId, Long tagId,
                                   Boolean favorite, int page, int size) {
        String normalizedQuery = query == null || query.isBlank() ? null : query.trim();
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return NotePageResponse.from(noteRepository
                .search(ownerId, normalizedQuery, folderId, tagId, favorite, PageRequest.of(safePage, safeSize))
                .map(NoteResponse::from));
    }

    @Transactional
    public NoteResponse create(Long ownerId, NoteRequest request) {
        User owner = userRepository.getReferenceById(ownerId);
        Note note = new Note(owner, request.title().trim(), request.content().trim(), request.favorite(), request.pinned());
        note.replaceTags(requireOwnedTags(ownerId, request.tagIds()));
        note.moveToFolder(requireOwnedFolder(ownerId, request.folderId()));
        return NoteResponse.from(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse update(Long ownerId, Long id, NoteRequest request) {
        Note note = requireOwned(ownerId, id);
        note.update(request.title().trim(), request.content().trim(), request.favorite(), request.pinned());
        note.replaceTags(requireOwnedTags(ownerId, request.tagIds()));
        note.moveToFolder(requireOwnedFolder(ownerId, request.folderId()));
        return NoteResponse.from(note);
    }

    @Transactional
    public void delete(Long ownerId, Long id) { requireOwned(ownerId, id).delete(); }

    private Note requireOwned(Long ownerId, Long id) {
        return noteRepository.findByIdAndOwnerIdAndDeletedAtIsNull(id, ownerId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "找不到筆記"));
    }

    private Set<Tag> requireOwnedTags(Long ownerId, Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return Set.of();
        List<Tag> tags = tagRepository.findAllByIdInAndOwnerId(tagIds, ownerId);
        if (tags.size() != tagIds.size()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "部分標籤不存在或不屬於目前使用者");
        }
        return new LinkedHashSet<>(tags);
    }

    private Folder requireOwnedFolder(Long ownerId, Long folderId) {
        if (folderId == null) return null;
        return folderRepository.findByIdAndOwnerId(folderId, ownerId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "資料夾不存在或不屬於目前使用者"));
    }
}
