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

async function addTask(noteId) {
  const title = taskDrafts[noteId]?.trim()
  if (!title) return
  taskError.value = ''
  try {
    await subjects.addTask(noteId, title)
    await dashboard.fetch()
    taskDrafts[noteId] = ''
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

async function removeTask(noteId, taskId) {
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
        <RouterLink to="/notes">學習筆記</RouterLink>
      </nav>
      <div class="sidebar-user"><span class="avatar">{{ auth.user?.displayName?.slice(0, 1) }}</span><div><strong>{{ auth.user?.displayName }}</strong><small>@{{ auth.user?.username }}</small></div><button type="button" aria-label="登出" @click="logout">↗</button></div>
    </aside>

    <main class="dashboard-main">
      <header class="dashboard-header">
        <div><p class="eyebrow"><span></span>My note wall</p><h1>我的便利貼</h1><p>把靈感、待辦和學習提醒貼在這裡，隨時回來接著前進。</p></div>
        <button class="button button-primary" type="button" @click="openEditor()">新增便利貼 ＋</button>
      </header>
      <section class="dashboard-summary" aria-label="學習摘要" :aria-busy="dashboard.loading">
        <article><span>科目</span><strong>{{ dashboard.summary?.subjectCount ?? '—' }}</strong></article>
        <article><span>學習筆記</span><strong>{{ dashboard.summary?.noteCount ?? '—' }}</strong></article>
        <article><span>待完成任務</span><strong>{{ dashboard.summary?.pendingTaskCount ?? '—' }}</strong></article>
      </section>
      <section v-if="dashboard.summary" class="dashboard-recent" aria-label="最近學習動態">
        <article><div class="recent-heading"><h2>最近筆記</h2><RouterLink to="/notes">查看全部</RouterLink></div><ul v-if="dashboard.summary.recentNotes?.length"><li v-for="note in dashboard.summary.recentNotes" :key="note.id"><strong>{{ note.title }}</strong><span>{{ new Date(note.updatedAt).toLocaleDateString('zh-TW') }}</span></li></ul><p v-else>目前還沒有學習筆記。</p></article>
        <article><div class="recent-heading"><h2>待完成任務</h2></div><ul v-if="dashboard.summary.pendingTasks?.length"><li v-for="task in dashboard.summary.pendingTasks" :key="task.id"><strong>{{ task.title }}</strong><span>待完成</span></li></ul><p v-else>目前沒有待完成任務。</p></article>
      </section>
      <div v-if="dashboard.error" class="dashboard-alert" role="alert">{{ dashboard.error }} <button type="button" @click="dashboard.fetch()">重新載入摘要</button></div>
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
                <span>{{ task.title }}</span>
                <button type="button" class="task-delete" :aria-label="`刪除「${task.title}」`" @click="removeTask(note.id, task.id)">×</button>
              </li>
            </ul>
            <form class="add-task" @submit.prevent="addTask(note.id)"><input v-model="taskDrafts[note.id]" :aria-label="`新增「${note.name}」的待辦事項`" maxlength="255" placeholder="新增待辦事項…"><button type="submit" aria-label="新增待辦事項">＋</button></form>
          </div>
        </article>
        <p v-if="taskError" class="task-error" role="alert">{{ taskError }}</p>
      </section>
      <section v-else class="empty-state"><span class="empty-illustration"><svg viewBox="0 0 80 80"><path d="M16 17h37a11 11 0 0 1 11 11v38H27A11 11 0 0 1 16 55V17Z"/><path d="M27 66a11 11 0 0 1 11-11h26M28 31h24M28 40h18"/></svg></span><p class="eyebrow"><span></span>Your first note</p><h2>從第一張便利貼開始</h2><p>把待辦、靈感或學習提醒寫下來，讓重要的事一直留在眼前。</p><button class="button button-primary" type="button" @click="openEditor()">建立第一張便利貼</button></section>
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
