<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNoteStore } from '../stores/notes'
import { primeCsrfToken } from '../api/client'
import RichTextEditor from '../components/RichTextEditor.vue'
import RichTextViewer from '../components/RichTextViewer.vue'

const auth = useAuthStore(), notes = useNoteStore(), router = useRouter()
const selectedId = ref(null), editorOpen = ref(false), deleting = ref(null), saving = ref(false), formError = ref('')
const blank = { id: null, title: '', content: '', favorite: false, pinned: false }
const form = reactive({ ...blank })
const selectedNote = computed(() => notes.items.find(note => note.id === selectedId.value) || null)

onMounted(async () => {
  try { await primeCsrfToken(); await notes.fetchAll(); selectedId.value = notes.items[0]?.id ?? null }
  catch (error) { if ([401, 403].includes(error.status)) router.push({ path: '/login', query: { redirect: '/notes' } }) }
})

function selectNote(id) { selectedId.value = id }
function openEditor(note = selectedNote.value) { Object.assign(form, note ? { ...note } : blank); formError.value = ''; editorOpen.value = true }
function dateLabel(value) { return value ? new Intl.DateTimeFormat('zh-TW', { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(value)) : '' }
async function save() { formError.value = ''; saving.value = true; try { const saved = await notes.save({ ...form }); selectedId.value = saved.id; editorOpen.value = false } catch (error) { formError.value = error.fieldErrors?.[0]?.message || error.message } finally { saving.value = false } }
async function remove() { const nextId = notes.items.find(note => note.id !== deleting.value.id)?.id ?? null; try { await notes.remove(deleting.value.id); selectedId.value = nextId; deleting.value = null } catch (error) { notes.error = error.message } }
async function logout() { await auth.logout(); router.push('/login') }
</script>

<template>
  <div class="dashboard-shell">
    <aside class="dashboard-sidebar">
      <RouterLink class="wordmark dashboard-brand" to="/"><span class="wordmark-mark">K</span><span>Kumi Study</span></RouterLink>
      <nav aria-label="主要導覽">
        <RouterLink to="/dashboard"><svg viewBox="0 0 24 24"><rect x="4" y="4" width="6" height="6" rx="1"/><rect x="14" y="4" width="6" height="6" rx="1"/><rect x="4" y="14" width="6" height="6" rx="1"/><rect x="14" y="14" width="6" height="6" rx="1"/></svg>我的便利貼</RouterLink>
        <RouterLink class="active" to="/notes"><svg viewBox="0 0 24 24"><path d="M6 3.5h12v17H6z"/><path d="M9 8h6M9 12h6M9 16h4"/></svg>學習筆記</RouterLink>
      </nav>
      <div class="sidebar-user"><span class="avatar">{{ auth.user?.displayName?.slice(0, 1) }}</span><div><strong>{{ auth.user?.displayName }}</strong><small>@{{ auth.user?.username }}</small></div><button type="button" aria-label="登出" @click="logout">↗</button></div>
    </aside>
    <main class="dashboard-main note-workspace-main">
      <header class="notes-workspace-header"><div><p class="eyebrow"><span></span>Learning notes</p><h1>學習筆記</h1></div><button class="button button-primary" type="button" @click="openEditor(null)">新增筆記 ＋</button></header>
      <div v-if="notes.error" class="dashboard-alert" role="alert">{{ notes.error }} <button type="button" @click="notes.fetchAll()">重試</button></div>
      <div class="notes-workspace" :class="{ loading: notes.loading }">
        <aside class="notes-list-panel" aria-label="筆記選單">
          <div class="notes-list-heading"><strong>我的筆記</strong><span>{{ notes.items.length }}</span></div>
          <div v-if="notes.loading" class="note-list-skeleton"><span v-for="i in 5" :key="i"></span></div>
          <ul v-else-if="notes.items.length" class="notes-list">
            <li v-for="note in notes.items" :key="note.id"><button type="button" :class="{ selected: note.id === selectedId }" :aria-current="note.id === selectedId ? 'page' : undefined" @click="selectNote(note.id)"><span class="note-list-title">{{ note.title }}</span><small>{{ dateLabel(note.updatedAt) }}</small><span v-if="note.pinned" class="note-list-pin">置頂</span></button></li>
          </ul>
          <div v-else class="notes-list-empty">尚未建立筆記</div>
        </aside>
        <article v-if="selectedNote" class="note-reader" aria-live="polite">
          <header><div><p class="eyebrow"><span></span>{{ selectedNote.pinned ? 'Pinned note' : 'Learning note' }}</p><h2>{{ selectedNote.title }}</h2><p class="note-reader-date">最後更新於 {{ dateLabel(selectedNote.updatedAt) }}</p></div><div class="note-reader-actions"><span v-if="selectedNote.favorite" class="favorite-label">已收藏</span><button type="button" class="button secondary-button" @click="openEditor(selectedNote)">編輯筆記</button><button type="button" class="reader-delete" aria-label="刪除目前筆記" @click="deleting = selectedNote">刪除</button></div></header>
          <div class="note-reader-content"><RichTextViewer :content="selectedNote.content" /></div>
        </article>
        <section v-else class="note-reader-empty"><span class="empty-illustration"><svg viewBox="0 0 80 80"><path d="M16 17h37a11 11 0 0 1 11 11v38H27A11 11 0 0 1 16 55V17Z"/><path d="M27 66a11 11 0 0 1 11-11h26M28 31h24M28 40h18"/></svg></span><h2>選擇一篇筆記</h2><p>從左側選單開啟筆記，或建立第一篇筆記。</p><button class="button button-primary" type="button" @click="openEditor(null)">建立筆記</button></section>
      </div>
    </main>
    <div v-if="editorOpen" class="modal-backdrop" @click.self="editorOpen = false"><section class="subject-modal note-editor" role="dialog" aria-modal="true" aria-labelledby="note-editor-title"><div class="modal-heading"><div><p class="eyebrow"><span></span>Learning note</p><h2 id="note-editor-title">{{ form.id ? '編輯筆記' : '新增筆記' }}</h2></div><button class="close-button" type="button" aria-label="關閉" @click="editorOpen = false">×</button></div><form @submit.prevent="save"><label>筆記標題<input v-model.trim="form.title" required maxlength="255" autofocus placeholder="例如：二元樹的走訪方式"></label><div class="rich-editor-field"><span>內容</span><RichTextEditor v-model="form.content" /></div><div class="note-options"><label><input v-model="form.pinned" type="checkbox">置頂這篇筆記</label><label><input v-model="form.favorite" type="checkbox">加入收藏</label></div><p v-if="formError" class="form-alert" role="alert">{{ formError }}</p><div class="modal-actions"><button class="button secondary-button" type="button" @click="editorOpen = false">取消</button><button class="button button-primary" type="submit" :disabled="saving">{{ saving ? '儲存中…' : '儲存筆記' }}</button></div></form></section></div>
    <div v-if="deleting" class="modal-backdrop"><section class="delete-dialog" role="alertdialog" aria-modal="true"><span class="delete-icon">!</span><h2>刪除「{{ deleting.title }}」？</h2><p>這篇筆記會被移除，刪除後無法復原。</p><div class="modal-actions"><button class="button secondary-button" @click="deleting = null">保留筆記</button><button class="button danger-button" @click="remove">確認刪除</button></div></section></div>
  </div>
</template>
