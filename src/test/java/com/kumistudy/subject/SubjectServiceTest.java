package com.kumistudy.subject;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.subject.dto.SubjectRequest;
import com.kumistudy.subject.dto.SubjectResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void create_shouldBindSubjectToCurrentUser() {
        SubjectService service = new SubjectService(subjectRepository, userRepository);
        User owner = new User("alice", "alice@example.com", "hash", "Alice");
        SubjectRequest request = new SubjectRequest("  資料結構  ", null, null, "完成樹與圖", 25, "複習第 3 章");
        when(userRepository.getReferenceById(7L)).thenReturn(owner);
        when(subjectRepository.save(org.mockito.ArgumentMatchers.any(Subject.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SubjectResponse response = service.create(7L, request);

        ArgumentCaptor<Subject> captor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(captor.capture());
        assertEquals("資料結構", response.name());
        assertEquals("#6F9987", response.color());
        assertEquals("book-open", response.icon());
    }

    @Test
    void create_shouldRejectDuplicateNameForSameOwner() {
        SubjectService service = new SubjectService(subjectRepository, userRepository);
        SubjectRequest request = new SubjectRequest("資料結構", "#112233", "book", null, 0, null);
        when(subjectRepository.existsByOwnerIdAndNameIgnoreCaseAndDeletedAtIsNull(7L, "資料結構"))
                .thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () -> service.create(7L, request));

        assertEquals(ErrorCode.RESOURCE_CONFLICT, exception.getErrorCode());
    }

    @Test
    void get_shouldOnlyQueryBySubjectIdAndCurrentOwner() {
        SubjectService service = new SubjectService(subjectRepository, userRepository);
        when(subjectRepository.findByIdAndOwnerIdAndDeletedAtIsNull(99L, 7L)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> service.get(7L, 99L));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verify(subjectRepository).findByIdAndOwnerIdAndDeletedAtIsNull(99L, 7L);
    }

    @Test
    void list_shouldOnlyReturnCurrentOwnersSubjects() {
        SubjectService service = new SubjectService(subjectRepository, userRepository);
        when(subjectRepository.findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(7L))
                .thenReturn(List.of());

        assertEquals(List.of(), service.list(7L));
        verify(subjectRepository).findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(7L);
    }
}
