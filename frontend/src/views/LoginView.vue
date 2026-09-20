<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthShell from '../components/AuthShell.vue'
import FormField from '../components/FormField.vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore(); const router = useRouter(); const route = useRoute()
const form = reactive({ username: '', password: '' })
const errors = reactive({}); const alert = ref(''); const loading = ref(false)

async function submit() {
  Object.keys(errors).forEach(key => delete errors[key]); alert.value = ''; loading.value = true
  try { await auth.login(form); router.push('/') }
  catch (error) { alert.value = error.message; error.fieldErrors?.forEach(item => { errors[item.field] = item.message }) }
  finally { loading.value = false }
}
</script>

<template><AuthShell mode="login"><h2>歡迎回來</h2><p class="auth-subtitle">輸入帳號和密碼，繼續今天的學習。</p><p v-if="route.query.registered" class="status-message">帳號建立成功，請登入開始使用。</p><form id="auth-form" class="auth-form" @submit.prevent="submit"><FormField id="username" v-model="form.username" label="使用者名稱" autocomplete="username" :error="errors.username"/><FormField id="password" v-model="form.password" label="密碼" type="password" autocomplete="current-password" :error="errors.password"/><p class="form-alert" role="alert" aria-live="polite">{{ alert }}</p><button class="button button-primary auth-submit" type="submit" :disabled="loading">{{ loading ? '登入中…' : '登入學習空間 →' }}</button></form><p class="auth-switch">還沒有帳號？ <RouterLink to="/register">免費建立帳號</RouterLink></p></AuthShell></template>
