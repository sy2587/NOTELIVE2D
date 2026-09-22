import { defineStore } from 'pinia'
import { apiRequest } from '../api/client'
import { notifyLive2D } from '../utils/live2d'

export const useSubjectStore = defineStore('subjects', {
  state: () => ({ items: [], tasksBySubject: {}, loading: false, error: '' }),
  actions: {
    async fetchAll() {
      this.loading = true; this.error = ''
      try {
        const items = await apiRequest('/api/subjects')
        this.items = Array.isArray(items) ? items : []
      }
      catch (error) { this.error = error.message; throw error }
      finally { this.loading = false }
    },
    async save(subject) {
      const creating = !subject.id
      const saved = await apiRequest(subject.id ? `/api/subjects/${subject.id}` : '/api/subjects', { method: subject.id ? 'PUT' : 'POST', body: JSON.stringify(subject) })
      if (!Array.isArray(this.items)) this.items = []
      const index = this.items.findIndex(item => item.id === saved.id)
      if (index >= 0) this.items[index] = saved; else this.items.unshift(saved)
      notifyLive2D(creating ? '新的便利貼貼好了，從一個小步驟開始吧！' : '便利貼已更新，目標變得更清楚了。')
    },
    async remove(id) {
      await apiRequest(`/api/subjects/${id}`, { method: 'DELETE' })
      this.items = Array.isArray(this.items) ? this.items.filter(item => item.id !== id) : []
      delete this.tasksBySubject[id]
    },
    async fetchTasks(subjectId) {
      const tasks = await apiRequest(`/api/subjects/${subjectId}/tasks`)
      this.tasksBySubject[subjectId] = Array.isArray(tasks) ? tasks : []
    },
    async addTask(subjectId, draft) {
      const task = await apiRequest(`/api/subjects/${subjectId}/tasks`, { method: 'POST', body: JSON.stringify(draft) })
      const tasks = this.tasksBySubject[subjectId] || []
      this.tasksBySubject[subjectId] = [...tasks, task]
      notifyLive2D('任務記下來了，別忘了幫它設定優先順序。')
    },
    async toggleTask(subjectId, taskId) {
      const updated = await apiRequest(`/api/subjects/${subjectId}/tasks/${taskId}/toggle`, { method: 'POST' })
      this.tasksBySubject[subjectId] = (this.tasksBySubject[subjectId] || []).map(task => task.id === taskId ? updated : task)
      notifyLive2D(updated.completed ? '完成一件了，很棒！穩穩地繼續前進吧。' : '任務已恢復為待完成。')
    },
    async updateTask(subjectId, taskId, draft) {
      const updated = await apiRequest(`/api/subjects/${subjectId}/tasks/${taskId}`, { method: 'PUT', body: JSON.stringify(draft) })
      this.tasksBySubject[subjectId] = (this.tasksBySubject[subjectId] || []).map(task => task.id === taskId ? updated : task)
      return updated
    },
    async removeTask(subjectId, taskId) {
      await apiRequest(`/api/subjects/${subjectId}/tasks/${taskId}`, { method: 'DELETE' })
      this.tasksBySubject[subjectId] = (this.tasksBySubject[subjectId] || []).filter(task => task.id !== taskId)
    }
  }
})
