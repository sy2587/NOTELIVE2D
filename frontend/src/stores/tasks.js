import { defineStore } from 'pinia'
import { apiRequest } from '../api/client'
import { notifyLive2D } from '../utils/live2d'

export const useTaskStore = defineStore('tasks', {
  state: () => ({ items: [], loading: false, error: '' }),
  actions: {
    async fetchPending() {
      this.loading = true
      this.error = ''
      try {
        const tasks = await apiRequest('/api/tasks/pending')
        this.items = Array.isArray(tasks) ? tasks : []
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },
    async toggle(task) {
      await apiRequest(`/api/subjects/${task.subjectId}/tasks/${task.id}/toggle`, { method: 'POST' })
      this.items = this.items.filter(item => item.id !== task.id)
      notifyLive2D('完成一件了，很棒！讓自己喘口氣再繼續。')
    },
    async update(task, draft) {
      const saved = await apiRequest(`/api/subjects/${task.subjectId}/tasks/${task.id}`, {
        method: 'PUT',
        body: JSON.stringify(draft)
      })
      const index = this.items.findIndex(item => item.id === saved.id)
      if (index >= 0) this.items[index] = saved
      notifyLive2D('任務已更新，接下來就照新節奏前進吧。', false)
      return saved
    },
    async remove(task) {
      await apiRequest(`/api/subjects/${task.subjectId}/tasks/${task.id}`, { method: 'DELETE' })
      this.items = this.items.filter(item => item.id !== task.id)
    }
  }
})
