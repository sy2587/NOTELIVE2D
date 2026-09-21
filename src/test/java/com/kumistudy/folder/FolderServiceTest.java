package com.kumistudy.folder;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.folder.dto.FolderRequest;
import com.kumistudy.note.NoteRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {
    @Mock FolderRepository folderRepository;
    @Mock UserRepository userRepository;
    @Mock NoteRepository noteRepository;

    @Test
    void create_shouldTrimNameAndBindOwner() {
        FolderService service = new FolderService(folderRepository, userRepository, noteRepository);
        when(userRepository.getReferenceById(7L)).thenReturn(new User("alice", "a@example.com", "hash", "Alice"));
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertEquals("Java", service.create(7L, new FolderRequest("  Java  ")).name());
    }

    @Test
    void create_shouldRejectDuplicateName() {
        FolderService service = new FolderService(folderRepository, userRepository, noteRepository);
        when(folderRepository.existsByOwnerIdAndNameIgnoreCase(7L, "Java")).thenReturn(true);
        ApiException error = assertThrows(ApiException.class, () -> service.create(7L, new FolderRequest("Java")));
        assertEquals(ErrorCode.RESOURCE_CONFLICT, error.getErrorCode());
    }

    @Test
    void delete_shouldUnlinkNotesBeforeFolder() {
        FolderService service = new FolderService(folderRepository, userRepository, noteRepository);
        Folder folder = new Folder(new User("alice", "a@example.com", "hash", "Alice"), "Java");
        when(folderRepository.findByIdAndOwnerId(3L, 7L)).thenReturn(Optional.of(folder));
        service.delete(7L, 3L);
        var order = inOrder(noteRepository, folderRepository);
        order.verify(noteRepository).clearFolder(7L, 3L);
        order.verify(folderRepository).delete(folder);
    }
}
