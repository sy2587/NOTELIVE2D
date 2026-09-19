// TODO: 負責待辦新增、列表、完成與逾期狀態顯示。

async function loadTasks(view = "today") {
    // TODO: 呼叫 GET /api/tasks?view=...，依日期與狀態渲染。
}

async function completeTask(taskId) {
    // TODO: 呼叫 PATCH /api/tasks/{id}/complete，成功後只更新該任務畫面。
}
