package com.kumistudy.dashboard;

import com.kumistudy.note.dto.NoteResponse;
import com.kumistudy.task.dto.TaskResponse;
import java.util.List;

public record DashboardResponse(
        long subjectCount,
        long noteCount,
        long pendingTaskCount,
        List<NoteResponse> recentNotes,
        List<TaskResponse> pendingTasks
) { }
