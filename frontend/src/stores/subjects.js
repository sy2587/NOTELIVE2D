import { defineStore } from 'pinia'
import { apiRequest } from '../api/client'

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
      const saved = await apiRequest(subject.id ? `/api/subjects/${subject.id}` : '/api/subjects', { method: subject.id ? 'PUT' : 'POST', body: JSON.stringify(subject) })
      if (!Array.isArray(this.items)) this.items = []
      const index = this.items.findIndex(item => item.id === saved.id)
      if (index >= 0) this.items[index] = saved; else this.items.unshift(saved)
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
    async addTask(subjectId, title) {
      const task = await apiRequest(`/api/subjects/${subjectId}/tasks`, { method: 'POST', body: JSON.stringify({ title }) })
      const tasks = this.tasksBySubject[subjectId] || []
      this.tasksBySubject[subjectId] = [...tasks, task]
    },
    async toggleTask(subjectId, taskId) {
      const updated = await apiRequest(`/api/subjects/${subjectId}/tasks/${taskId}/toggle`, { method: 'POST' })
      this.tasksBySubject[subjectId] = (this.tasksBySubject[subjectId] || []).map(task => task.id === taskId ? updated : task)
    },
    async removeTask(subjectId, taskId) {
      await apiRequest(`/api/subjects/${subjectId}/tasks/${taskId}`, { method: 'DELETE' })
      this.tasksBySubject[subjectId] = (this.tasksBySubject[subjectId] || []).filter(task => task.id !== taskId)
    }
  }
})
