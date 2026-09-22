<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useSubjectStore } from '../stores/subjects'
import { useDashboardStore } from '../stores/dashboard'
import { primeCsrfToken } from '../api/client'

const auth = useAuthStore()
const subjects = useSubjectStore()
const dashboard = useDashboardStore()
const router = useRouter()
const editorOpen = ref(false)
const activePanel = ref('notes')
const deleting = ref(null)
const saving = ref(false)
const formError = ref('')
const taskDrafts = reactive({})
const taskError = ref('')
const blank = { id: null, name: '', color: '#E9F1D5', studyGoal: '', progress: 0, description: '', icon: 'sticky-note' }
const form = reactive({ ...blank })
const colors = ['#E9F1D5', '#F8D7B7', '#CDE3E8', '#F2D8A7', '#DDD2E7', '#CBE4DB']

onMounted(async () => {
  try {
    await primeCsrfToken()
    await Promise.all([subjects.fetchAll(), dashboard.fetch()])
    await Promise.all(subjects.items.map(subject => subjects.fetchTasks(subject.id)))
  } catch (error) {
    if ([401, 403].includes(error.status)) router.push({ path: '/login', query: { redirect: '/dashboard' } })
  }
})

function openEditor(note) {
  Object.assign(form, note ? { ...note } : blank)
  formError.value = ''
  editorOpen.value = true
}

function noteTilt(id) {
  return `${((Number(id) || 0) % 5 - 2) * 0.45}deg`
}

function tasksFor(noteId) {
  return subjects.tasksBySubject[noteId] || []
}

function taskDraft(noteId) {
  if (!taskDrafts[noteId]) taskDrafts[noteId] = { title: '', priority: 'MEDIUM', dueDate: '' }
  return taskDrafts[noteId]
}

function taskDueLabel(task) {
  if (!task.dueDate) return ''
  if (task.overdue) return `已逾期 · ${task.dueDate}`
  if (task.dueToday) return '今天到期'
  return task.dueDate
}

async function addTask(noteId) {
  const draft = taskDraft(noteId)
  const title = draft.title.trim()
  if (!title) return
  taskError.value = ''
  try {
    await subjects.addTask(noteId, { title, priority: draft.priority, dueDate: draft.dueDate || null })
    await dashboard.fetch()
    taskDrafts[noteId] = { title: '', priority: 'MEDIUM', dueDate: '' }
  } catch (error) {
    taskError.value = error.message
  }
}

async function toggleTask(noteId, taskId) {
  taskError.value = ''
  try {
    await subjects.toggleTask(noteId, taskId)
    await dashboard.fetch()
  } catch (error) {
    taskError.value = error.message
  }
}

async function editTask(noteId, task) {
  const title = window.prompt('修改待辦事項', task.title)?.trim()
  if (!title) return
  const priorityInput = window.prompt('優先級：LOW、MEDIUM 或 HIGH', task.priority || 'MEDIUM')?.trim().toUpperCase()
  if (priorityInput == null) return
  if (!['LOW', 'MEDIUM', 'HIGH'].includes(priorityInput)) {
    taskError.value = '優先級必須是 LOW、MEDIUM 或 HIGH'
    return
  }
  const dueDate = window.prompt('到期日（YYYY-MM-DD，留空表示不設定）', task.dueDate || '')
  if (dueDate == null) return
  if (dueDate && !/^\d{4}-\d{2}-\d{2}$/.test(dueDate)) {
    taskError.value = '到期日格式必須是 YYYY-MM-DD'
    return
  }
  taskError.value = ''
  try {
    await subjects.updateTask(noteId, task.id, { title, description: task.description, priority: priorityInput, dueDate: dueDate || null })
    await dashboard.fetch()
  } catch (error) { taskError.value = error.fieldErrors?.[0]?.message || error.message }
}

async function removeTask(noteId, taskId, taskTitle = '') {
  if (!window.confirm(`確定刪除${taskTitle ? `「${taskTitle}」` : '這項待辦事項'}？刪除後無法復原。`)) return
  taskError.value = ''
  try {
    await subjects.removeTask(noteId, taskId)
    await dashboard.fetch()
  } catch (error) {
    taskError.value = error.message
  }
}

async function save() {
  formError.value = ''
  saving.value = true
  try {
    await subjects.save({ ...form, progress: form.progress ?? 0 })
    await dashboard.fetch()
    editorOpen.value = false
  } catch (error) {
    formError.value = error.fieldErrors?.[0]?.message || error.message
  } finally {
    saving.value = false
  }
}

async function remove() {
  try {
    await subjects.remove(deleting.value.id)
    await dashboard.fetch()
    deleting.value = null
  } catch (error) {
    subjects.error = error.message
  }
}

async function logout() {
  await auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="dashboard-shell">
    <aside class="dashboard-sidebar">
      <RouterLink class="wordmark dashboard-brand" to="/"><span class="wordmark-mark">K</span><span>Kumi Study</span></RouterLink>
      <nav aria-label="主要導覽">
        <RouterLink class="active" to="/dashboard">
          <svg viewBox="0 0 24 24"><rect x="4" y="4" width="6" height="6" rx="1"/><rect x="14" y="4" width="6" height="6" rx="1"/><rect x="4" y="14" width="6" height="6" rx="1"/><rect x="14" y="14" width="6" height="6" rx="1"/></svg>
          我的便利貼
        </RouterLink>
        <RouterLink to="/notes"><svg viewBox="0 0 24 24"><path d="M6 3.5h12v17H6z"/><path d="M9 8h6M9 12h6M9 16h4"/></svg>學習筆記</RouterLink>
        <RouterLink to="/tasks"><svg viewBox="0 0 24 24"><rect x="4" y="4" width="16" height="16" rx="3"/><path d="m8 12 2.3 2.3L16 8.7"/></svg>任務管理</RouterLink>
      </nav>
      <div class="sidebar-user"><span class="avatar">{{ auth.user?.displayName?.slice(0, 1) }}</span><div><strong>{{ auth.user?.displayName }}</strong><small>@{{ auth.user?.username }}</small></div><button type="button" aria-label="登出" @click="logout">↗</button></div>
    </aside>

    <main class="dashboard-main">
      <div class="dashboard-content">
      <header class="dashboard-header">
        <div><p class="eyebrow"><span></span>{{ activePanel === 'notes' ? 'My note wall' : 'Study overview' }}</p><h1>{{ activePanel === 'notes' ? '我的便利貼' : '學習總覽' }}</h1><p>{{ activePanel === 'notes' ? '把靈感、待辦和學習提醒貼在這裡，隨時回來接著前進。' : '快速查看筆記、今日任務與待完成事項。' }}</p></div>
        <button v-if="activePanel === 'notes'" class="button button-primary" type="button" @click="openEditor()">新增便利貼 ＋</button>
      </header>
      <div class="dashboard-tabs" role="tablist" aria-label="便利貼與學習總覽">
        <button id="notes-tab" type="button" role="tab" :class="{ active: activePanel === 'notes' }" :aria-selected="activePanel === 'notes'" aria-controls="notes-panel" @click="activePanel = 'notes'">便利貼</button>
        <button id="overview-tab" type="button" role="tab" :class="{ active: activePanel === 'overview' }" :aria-selected="activePanel === 'overview'" aria-controls="overview-panel" @click="activePanel = 'overview'">總覽</button>
      </div>

      <div v-if="activePanel === 'overview'" id="overview-panel" role="tabpanel" aria-labelledby="overview-tab">
        <section class="dashboard-summary" aria-label="學習摘要" :aria-busy="dashboard.loading">
          <article><span>科目</span><strong>{{ dashboard.summary?.subjectCount ?? '—' }}</strong></article>
          <article><span>學習筆記</span><strong>{{ dashboard.summary?.noteCount ?? '—' }}</strong></article>
          <article><span>待完成任務</span><strong>{{ dashboard.summary?.pendingTaskCount ?? '—' }}</strong></article>
          <article class="today-summary"><span>今天到期</span><strong>{{ dashboard.summary?.todayTaskCount ?? '—' }}</strong></article>
          <article :class="{ 'overdue-summary': dashboard.summary?.overdueTaskCount > 0 }"><span>已逾期</span><strong>{{ dashboard.summary?.overdueTaskCount ?? '—' }}</strong></article>
        </section>
        <section v-if="dashboard.summary" class="dashboard-recent" aria-label="最近學習動態">
          <article><div class="recent-heading"><h2>最近筆記</h2><RouterLink to="/notes">查看全部</RouterLink></div><ul v-if="dashboard.summary.recentNotes?.length" class="overview-note-list"><li v-for="note in dashboard.summary.recentNotes" :key="note.id"><RouterLink :to="{ name: 'notes', query: { note: note.id } }"><strong>{{ note.title }}</strong><span>{{ new Date(note.updatedAt).toLocaleDateString('zh-TW') }}</span></RouterLink></li></ul><p v-else>目前還沒有學習筆記。</p></article>
          <article><div class="recent-heading"><h2>今日任務</h2></div><ul v-if="dashboard.summary.todayTasks?.length" class="overview-task-list"><li v-for="task in dashboard.summary.todayTasks" :key="task.id"><button type="button" class="overview-task-check" :aria-label="`完成「${task.title}」`" @click="toggleTask(task.subjectId, task.id)">✓</button><span class="overview-task-copy"><strong>{{ task.title }}</strong><small>{{ task.priority === 'HIGH' ? '高優先' : task.priority === 'LOW' ? '低優先' : '中優先' }}</small></span><span class="overview-row-actions"><button type="button" :aria-label="`編輯「${task.title}」`" @click="editTask(task.subjectId, task)">編輯</button><button type="button" class="danger-link" :aria-label="`刪除「${task.title}」`" @click="removeTask(task.subjectId, task.id, task.title)">刪除</button></span></li></ul><p v-else>今天沒有到期任務。</p></article>
          <article><div class="recent-heading"><h2>待完成任務</h2></div><ul v-if="dashboard.summary.pendingTasks?.length" class="overview-task-list"><li v-for="task in dashboard.summary.pendingTasks" :key="task.id"><button type="button" class="overview-task-check" :aria-label="`完成「${task.title}」`" @click="toggleTask(task.subjectId, task.id)">✓</button><span class="overview-task-copy"><strong>{{ task.title }}</strong><small :class="{ 'overdue-text': task.overdue }">{{ taskDueLabel(task) || '待完成' }}</small></span><span class="overview-row-actions"><button type="button" :aria-label="`編輯「${task.title}」`" @click="editTask(task.subjectId, task)">編輯</button><button type="button" class="danger-link" :aria-label="`刪除「${task.title}」`" @click="removeTask(task.subjectId, task.id, task.title)">刪除</button></span></li></ul><p v-else>目前沒有待完成任務。</p></article>
        </section>
        <div v-if="taskError" class="dashboard-alert" role="alert">{{ taskError }}</div>
        <div v-if="dashboard.error" class="dashboard-alert" role="alert">{{ dashboard.error }} <button type="button" @click="dashboard.fetch()">重新載入摘要</button></div>
      </div>

      <div v-else id="notes-panel" role="tabpanel" aria-labelledby="notes-tab">
        <div v-if="subjects.error" class="dashboard-alert" role="alert">{{ subjects.error }} <button type="button" @click="subjects.fetchAll()">重試</button></div>
        <div v-if="subjects.loading" class="note-grid"><div v-for="i in 3" :key="i" class="sticky-note skeleton"></div></div>
        <section v-else-if="subjects.items?.length" class="note-grid" aria-label="便利貼列表">
        <article v-for="note in subjects.items" :key="note.id" class="sticky-note" :style="{ '--note-color': note.color || '#E9F1D5', '--note-tilt': noteTilt(note.id) }">
          <div class="note-actions"><span v-if="note.studyGoal" class="note-reminder">{{ note.studyGoal }}</span><div><button type="button" @click="openEditor(note)">編輯</button><button class="danger-link" type="button" @click="deleting = note">刪除</button></div></div>
          <h2>{{ note.name }}</h2>
          <p v-if="note.description" class="note-content">{{ note.description }}</p>
          <p v-else class="note-placeholder">寫下想記住的事…</p>
          <div class="subject-progress" :aria-label="`學習進度 ${note.progress}%`"><span>學習進度</span><strong>{{ note.progress }}%</strong><div><i :style="{ width: `${note.progress}%` }"></i></div></div>
          <div class="note-tasks">
            <p class="task-heading">待辦事項 <span>{{ tasksFor(note.id).filter(task => task.completed).length }} / {{ tasksFor(note.id).length }}</span></p>
            <ul v-if="tasksFor(note.id).length" class="task-list" aria-label="待辦事項">
              <li v-for="task in tasksFor(note.id)" :key="task.id" :class="{ completed: task.completed }">
                <button type="button" class="task-check" :aria-label="task.completed ? `標示「${task.title}」為未完成` : `標示「${task.title}」為已完成`" :aria-pressed="task.completed" @click="toggleTask(note.id, task.id)">✓</button>
                <span><strong>{{ task.title }}</strong><small v-if="task.dueDate || task.priority !== 'MEDIUM'" :class="{ 'overdue-text': task.overdue }">{{ taskDueLabel(task) }}{{ taskDueLabel(task) && task.priority !== 'MEDIUM' ? ' · ' : '' }}{{ task.priority === 'HIGH' ? '高優先' : task.priority === 'LOW' ? '低優先' : '' }}</small></span>
                <span class="task-row-actions"><button type="button" class="task-edit" :aria-label="`編輯「${task.title}」`" @click="editTask(note.id, task)">✎</button><button type="button" class="task-delete" :aria-label="`刪除「${task.title}」`" @click="removeTask(note.id, task.id, task.title)">×</button></span>
              </li>
            </ul>
            <form class="add-task add-task-detailed" @submit.prevent="addTask(note.id)"><input v-model="taskDraft(note.id).title" :aria-label="`新增「${note.name}」的待辦事項`" maxlength="255" placeholder="新增待辦事項…"><select v-model="taskDraft(note.id).priority" aria-label="優先級"><option value="LOW">低</option><option value="MEDIUM">中</option><option value="HIGH">高</option></select><input v-model="taskDraft(note.id).dueDate" type="date" aria-label="到期日"><button type="submit" aria-label="新增待辦事項">＋</button></form>
          </div>
        </article>
        <p v-if="taskError" class="task-error" role="alert">{{ taskError }}</p>
        </section>
        <section v-else class="empty-state"><span class="empty-illustration"><svg viewBox="0 0 80 80"><path d="M16 17h37a11 11 0 0 1 11 11v38H27A11 11 0 0 1 16 55V17Z"/><path d="M27 66a11 11 0 0 1 11-11h26M28 31h24M28 40h18"/></svg></span><p class="eyebrow"><span></span>Your first note</p><h2>從第一張便利貼開始</h2><p>把待辦、靈感或學習提醒寫下來，讓重要的事一直留在眼前。</p><button class="button button-primary" type="button" @click="openEditor()">建立第一張便利貼</button></section>
      </div>
      </div>
    </main>

    <div v-if="editorOpen" class="modal-backdrop" @click.self="editorOpen = false">
      <section class="subject-modal" role="dialog" aria-modal="true" aria-labelledby="editor-title">
        <div class="modal-heading"><div><p class="eyebrow"><span></span>Sticky note</p><h2 id="editor-title">{{ form.id ? '編輯便利貼' : '新增便利貼' }}</h2></div><button class="close-button" type="button" aria-label="關閉" @click="editorOpen = false">×</button></div>
        <form @submit.prevent="save">
          <label>標題<input v-model.trim="form.name" required maxlength="150" autofocus placeholder="例如：下週要完成的事"></label>
          <fieldset><legend>便利貼顏色</legend><div class="color-options"><label v-for="color in colors" :key="color"><input v-model="form.color" type="radio" name="color" :value="color"><span :style="{ background: color }"></span></label></div></fieldset>
          <label>提醒事項 <span class="field-hint">選填</span><input v-model.trim="form.studyGoal" maxlength="100" placeholder="例如：週五前完成"></label>
          <label>學習進度 <output>{{ form.progress }}%</output><input v-model.number="form.progress" type="range" min="0" max="100" step="5"></label>
          <label>內容<textarea v-model.trim="form.description" maxlength="255" placeholder="寫下詳細內容、靈感或待辦事項…"></textarea></label>
          <p v-if="formError" class="form-alert" role="alert">{{ formError }}</p>
          <div class="modal-actions"><button class="button secondary-button" type="button" @click="editorOpen = false">取消</button><button class="button button-primary" type="submit" :disabled="saving">{{ saving ? '儲存中…' : '儲存便利貼' }}</button></div>
        </form>
      </section>
    </div>
    <div v-if="deleting" class="modal-backdrop"><section class="delete-dialog" role="alertdialog" aria-modal="true"><span class="delete-icon">!</span><h2>刪除「{{ deleting.name }}」？</h2><p>這張便利貼會從列表中移除，刪除後無法復原。</p><div class="modal-actions"><button class="button secondary-button" @click="deleting = null">保留便利貼</button><button class="button danger-button" @click="remove">確認刪除</button></div></section></div>
  </div>
</template>
