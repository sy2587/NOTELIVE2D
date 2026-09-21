package com.kumistudy.folder;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.folder.dto.FolderRequest;
import com.kumistudy.folder.dto.FolderResponse;
import com.kumistudy.note.NoteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public FolderService(FolderRepository folderRepository, UserRepository userRepository, NoteRepository noteRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    @Transactional(readOnly = true)
    public List<FolderResponse> list(Long ownerId) {
        return folderRepository.findAllByOwnerIdOrderByNameAsc(ownerId).stream().map(FolderResponse::from).toList();
    }

    @Transactional
    public FolderResponse create(Long ownerId, FolderRequest request) {
        String name = request.name().trim();
        ensureUnique(ownerId, name, null);
        User owner = userRepository.getReferenceById(ownerId);
        return FolderResponse.from(folderRepository.save(new Folder(owner, name)));
    }

    @Transactional
    public FolderResponse update(Long ownerId, Long folderId, FolderRequest request) {
        Folder folder = requireOwned(ownerId, folderId);
        String name = request.name().trim();
        ensureUnique(ownerId, name, folderId);
        folder.rename(name);
        return FolderResponse.from(folder);
    }

    @Transactional
    public void delete(Long ownerId, Long folderId) {
        Folder folder = requireOwned(ownerId, folderId);
        noteRepository.clearFolder(ownerId, folderId);
        folderRepository.delete(folder);
    }

    private Folder requireOwned(Long ownerId, Long folderId) {
        return folderRepository.findByIdAndOwnerId(folderId, ownerId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "找不到資料夾"));
    }

    private void ensureUnique(Long ownerId, String name, Long excludedId) {
        boolean exists = excludedId == null
                ? folderRepository.existsByOwnerIdAndNameIgnoreCase(ownerId, name)
                : folderRepository.existsByOwnerIdAndNameIgnoreCaseAndIdNot(ownerId, name, excludedId);
        if (exists) throw new ApiException(ErrorCode.RESOURCE_CONFLICT, "資料夾名稱已存在");
    }
}
