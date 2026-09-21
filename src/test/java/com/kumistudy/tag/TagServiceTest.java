package com.kumistudy.tag;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.tag.dto.TagRequest;
import java.util.Optional;
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
class TagServiceTest {
    @Mock TagRepository tagRepository;
    @Mock UserRepository userRepository;

    @Test
    void create_shouldTrimNameAndBindCurrentUser() {
        TagService service = new TagService(tagRepository, userRepository);
        when(userRepository.getReferenceById(7L)).thenReturn(new User("alice", "a@example.com", "hash", "Alice"));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals("Java", service.create(7L, new TagRequest("  Java  ")).name());
    }

    @Test
    void create_shouldRejectDuplicateNameForSameOwner() {
        TagService service = new TagService(tagRepository, userRepository);
        when(tagRepository.existsByOwnerIdAndNameIgnoreCase(7L, "Java")).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> service.create(7L, new TagRequest("Java")));

        assertEquals(ErrorCode.RESOURCE_CONFLICT, exception.getErrorCode());
    }

    @Test
    void delete_shouldClearNoteLinksBeforeDeletingTag() {
        TagService service = new TagService(tagRepository, userRepository);
        Tag tag = new Tag(new User("alice", "a@example.com", "hash", "Alice"), "Java");
        when(tagRepository.findByIdAndOwnerId(3L, 7L)).thenReturn(Optional.of(tag));

        service.delete(7L, 3L);

        verify(tagRepository).deleteNoteLinks(3L);
        verify(tagRepository).delete(tag);
    }
}
