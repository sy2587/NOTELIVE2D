<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useSubjectStore } from '../stores/subjects'
import { useTaskStore } from '../stores/tasks'
import { primeCsrfToken } from '../api/client'

const auth = useAuthStore()
const subjects = useSubjectStore()
const tasks = useTaskStore()
const router = useRouter()
const activeFilter = ref('all')
const subjectFilter = ref('')
const editing = ref(null)
const saving = ref(false)
const actionError = ref('')
const form = reactive({ title: '', description: '', priority: 'MEDIUM', dueDate: '' })

const counts = computed(() => ({
  all: tasks.items.length,
  today: tasks.items.filter(task => task.dueToday).length,
  overdue: tasks.items.filter(task => task.overdue).length
}))

const visibleTasks = computed(() => tasks.items
  .filter(task => activeFilter.value === 'all' || (activeFilter.value === 'today' ? task.dueToday : task.overdue))
  .filter(task => !subjectFilter.value || task.subjectId === Number(subjectFilter.value))
  .sort((a, b) => {
    if (a.overdue !== b.overdue) return a.overdue ? -1 : 1
    if (a.dueToday !== b.dueToday) return a.dueToday ? -1 : 1
    if (!a.dueDate && b.dueDate) return 1
    if (a.dueDate && !b.dueDate) return -1
    return (a.dueDate || '').localeCompare(b.dueDate || '')
  }))

onMounted(async () => {
  try {
    await primeCsrfToken()
    await Promise.all([tasks.fetchPending(), subjects.fetchAll()])
  } catch (error) {
    if ([401, 403].includes(error.status)) router.push({ path: '/login', query: { redirect: '/tasks' } })
  }
})

function subjectName(subjectId) {
  return subjects.items.find(subject => subject.id === subjectId)?.name || '未分類便利貼'
}

function dueLabel(task) {
  if (task.overdue) return `已逾期 · ${task.dueDate}`
  if (task.dueToday) return '今天到期'
  return task.dueDate || '未設定日期'
}

function openEditor(task) {
  editing.value = task
  Object.assign(form, {
    title: task.title,
    description: task.description || '',
    priority: task.priority || 'MEDIUM',
    dueDate: task.dueDate || ''
  })
  actionError.value = ''
}

async function complete(task) {
  actionError.value = ''
  try { await tasks.toggle(task) }
  catch (error) { actionError.value = error.message }
}

async function save() {
  saving.value = true
  actionError.value = ''
  try {
    await tasks.update(editing.value, { ...form, dueDate: form.dueDate || null })
    editing.value = null
  } catch (error) {
    actionError.value = error.fieldErrors?.[0]?.message || error.message
  } finally {
    saving.value = false
  }
}

async function remove(task) {
  if (!window.confirm(`確定刪除「${task.title}」？刪除後無法復原。`)) return
  actionError.value = ''
  try { await tasks.remove(task) }
  catch (error) { actionError.value = error.message }
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
        <RouterLink to="/dashboard"><svg viewBox="0 0 24 24"><rect x="4" y="4" width="6" height="6" rx="1"/><rect x="14" y="4" width="6" height="6" rx="1"/><rect x="4" y="14" width="6" height="6" rx="1"/><rect x="14" y="14" width="6" height="6" rx="1"/></svg>我的便利貼</RouterLink>
        <RouterLink to="/notes"><svg viewBox="0 0 24 24"><path d="M6 3.5h12v17H6z"/><path d="M9 8h6M9 12h6M9 16h4"/></svg>學習筆記</RouterLink>
        <RouterLink class="active" to="/tasks"><svg viewBox="0 0 24 24"><rect x="4" y="4" width="16" height="16" rx="3"/><path d="m8 12 2.3 2.3L16 8.7"/></svg>任務管理</RouterLink>
      </nav>
      <div class="sidebar-user"><span class="avatar">{{ auth.user?.displayName?.slice(0, 1) }}</span><div><strong>{{ auth.user?.displayName }}</strong><small>@{{ auth.user?.username }}</small></div><button type="button" aria-label="登出" @click="logout">↗</button></div>
    </aside>

    <main class="dashboard-main tasks-main">
      <div class="tasks-content">
        <header class="tasks-header"><div><p class="eyebrow"><span></span>Task planner</p><h1>任務管理</h1><p>集中查看所有便利貼的待辦事項。</p></div><span class="tasks-total">{{ counts.all }} <small>待完成</small></span></header>

        <div class="task-toolbar">
          <div class="task-filter-tabs" role="tablist" aria-label="任務篩選">
            <button v-for="filter in [{ id: 'all', label: '全部' }, { id: 'today', label: '今日到期' }, { id: 'overdue', label: '已逾期' }]" :key="filter.id" type="button" role="tab" :class="{ active: activeFilter === filter.id }" :aria-selected="activeFilter === filter.id" @click="activeFilter = filter.id">{{ filter.label }} <span>{{ counts[filter.id] }}</span></button>
          </div>
          <label class="task-subject-filter"><span>便利貼</span><select v-model="subjectFilter"><option value="">全部便利貼</option><option v-for="subject in subjects.items" :key="subject.id" :value="String(subject.id)">{{ subject.name }}</option></select></label>
        </div>

        <div v-if="tasks.error || actionError" class="dashboard-alert" role="alert">{{ actionError || tasks.error }} <button v-if="tasks.error" type="button" @click="tasks.fetchPending()">重試</button></div>
        <div v-if="tasks.loading" class="task-page-list" aria-label="正在載入任務"><div v-for="i in 4" :key="i" class="task-page-row skeleton"></div></div>
        <ul v-else-if="visibleTasks.length" class="task-page-list" aria-live="polite">
          <li v-for="task in visibleTasks" :key="task.id" class="task-page-row">
            <button type="button" class="task-complete-button" :aria-label="`完成「${task.title}」`" @click="complete(task)"><span>✓</span></button>
            <div class="task-page-copy"><strong>{{ task.title }}</strong><p v-if="task.description">{{ task.description }}</p><div><span>{{ subjectName(task.subjectId) }}</span><time :class="{ overdue: task.overdue }">{{ dueLabel(task) }}</time><span class="priority-chip" :class="`priority-${task.priority.toLowerCase()}`">{{ task.priority === 'HIGH' ? '高優先' : task.priority === 'LOW' ? '低優先' : '中優先' }}</span></div></div>
            <div class="task-page-actions"><button type="button" @click="openEditor(task)">編輯</button><button type="button" class="danger-link" @click="remove(task)">刪除</button></div>
          </li>
        </ul>
        <section v-else class="task-empty"><span aria-hidden="true">✓</span><h2>{{ activeFilter === 'all' ? '待辦事項都完成了' : '這個篩選沒有任務' }}</h2><p>可以回到便利貼新增下一個學習任務。</p><RouterLink class="button button-primary" to="/dashboard">回到便利貼</RouterLink></section>
      </div>
    </main>

    <div v-if="editing" class="modal-backdrop" @click.self="editing = null">
      <section class="subject-modal task-edit-modal" role="dialog" aria-modal="true" aria-labelledby="task-editor-title">
        <div class="modal-heading"><div><p class="eyebrow"><span></span>Edit task</p><h2 id="task-editor-title">編輯任務</h2></div><button class="close-button" type="button" aria-label="關閉" @click="editing = null">×</button></div>
        <form @submit.prevent="save">
          <label>任務名稱<input v-model.trim="form.title" required maxlength="255" autofocus></label>
          <label>說明 <span class="field-hint">選填</span><textarea v-model.trim="form.description" maxlength="1000"></textarea></label>
          <div class="form-row"><label>優先級<select v-model="form.priority"><option value="LOW">低</option><option value="MEDIUM">中</option><option value="HIGH">高</option></select></label><label>到期日<input v-model="form.dueDate" type="date"></label></div>
          <p v-if="actionError" class="form-alert" role="alert">{{ actionError }}</p>
          <div class="modal-actions"><button class="button secondary-button" type="button" @click="editing = null">取消</button><button class="button button-primary" type="submit" :disabled="saving">{{ saving ? '儲存中…' : '儲存任務' }}</button></div>
        </form>
      </section>
    </div>
  </div>
</template>
