package com.kumistudy.note;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.folder.FolderRepository;
import com.kumistudy.note.dto.NoteRequest;
import com.kumistudy.tag.TagRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
    @Mock NoteRepository noteRepository;
    @Mock UserRepository userRepository;
    @Mock TagRepository tagRepository;
    @Mock FolderRepository folderRepository;

    @Test
    void create_shouldBindNoteToCurrentUser() {
        NoteService service = new NoteService(noteRepository, userRepository, tagRepository, folderRepository);
        when(userRepository.getReferenceById(7L)).thenReturn(new User("alice", "a@example.com", "hash", "Alice"));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(7L, new NoteRequest("  Title  ", "  Content  ", true, false, Set.of(), null));

        assertEquals("Title", response.title());
        assertEquals("Content", response.content());
        verify(userRepository).getReferenceById(7L);
    }

    @Test
    void create_shouldRejectTagsOwnedByAnotherUser() {
        NoteService service = new NoteService(noteRepository, userRepository, tagRepository, folderRepository);
        when(userRepository.getReferenceById(7L)).thenReturn(new User("alice", "a@example.com", "hash", "Alice"));
        when(tagRepository.findAllByIdInAndOwnerId(Set.of(3L), 7L)).thenReturn(List.of());

        ApiException exception = assertThrows(ApiException.class,
                () -> service.create(7L, new NoteRequest("Title", "Content", false, false, Set.of(3L), null)));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void get_shouldOnlyQueryCurrentOwnersNote() {
        NoteService service = new NoteService(noteRepository, userRepository, tagRepository, folderRepository);
        when(noteRepository.findByIdAndOwnerIdAndDeletedAtIsNull(9L, 7L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.get(7L, 9L));
        verify(noteRepository).findByIdAndOwnerIdAndDeletedAtIsNull(9L, 7L);
    }
}
