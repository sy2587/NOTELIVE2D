package com.kumistudy.note;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.note.dto.NoteRequest;
import com.kumistudy.note.dto.NoteResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    public NoteService(NoteRepository noteRepository, UserRepository userRepository) { this.noteRepository = noteRepository; this.userRepository = userRepository; }
    @Transactional(readOnly = true) public List<NoteResponse> list(Long ownerId) { return noteRepository.findAllByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(ownerId).stream().map(NoteResponse::from).toList(); }
    @Transactional(readOnly = true) public NoteResponse get(Long ownerId, Long id) { return NoteResponse.from(requireOwned(ownerId, id)); }
    @Transactional public NoteResponse create(Long ownerId, NoteRequest request) { User owner = userRepository.getReferenceById(ownerId); return NoteResponse.from(noteRepository.save(new Note(owner, request.title().trim(), request.content().trim(), request.favorite(), request.pinned()))); }
    @Transactional public NoteResponse update(Long ownerId, Long id, NoteRequest request) { Note note = requireOwned(ownerId, id); note.update(request.title().trim(), request.content().trim(), request.favorite(), request.pinned()); return NoteResponse.from(note); }
    @Transactional public void delete(Long ownerId, Long id) { requireOwned(ownerId, id).delete(); }
    private Note requireOwned(Long ownerId, Long id) { return noteRepository.findByIdAndOwnerIdAndDeletedAtIsNull(id, ownerId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "找不到此筆記")); }
}
