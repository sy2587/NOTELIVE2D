<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthShell from '../components/AuthShell.vue'
import FormField from '../components/FormField.vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore(); const router = useRouter()
const form = reactive({ displayName: '', username: '', email: '', password: '', confirmPassword: '' })
const errors = reactive({}); const alert = ref(''); const loading = ref(false)

async function submit() {
  Object.keys(errors).forEach(key => delete errors[key]); alert.value = ''
  if (form.password !== form.confirmPassword) { errors.confirmPassword = '兩次輸入的密碼不一致'; alert.value = errors.confirmPassword; return }
  loading.value = true
  try {
    const { confirmPassword, ...payload } = form
    await auth.register(payload)
    router.push({ name: 'login', query: { registered: 'true' } })
  } catch (error) { alert.value = error.message; error.fieldErrors?.forEach(item => { errors[item.field] = item.message }) }
  finally { loading.value = false }
}
</script>

<template><AuthShell mode="register"><h2>建立學習空間</h2><p class="auth-subtitle">免費建立帳號，不需要信用卡。</p><form id="auth-form" class="auth-form" @submit.prevent="submit"><div class="form-row"><FormField id="displayName" v-model="form.displayName" label="顯示名稱" autocomplete="name" :minlength="2" :error="errors.displayName"/><FormField id="username" v-model="form.username" label="使用者名稱" autocomplete="username" :minlength="3" :error="errors.username"/></div><FormField id="email" v-model="form.email" label="電子信箱" type="email" autocomplete="email" :error="errors.email"/><div class="form-row"><FormField id="password" v-model="form.password" label="密碼" type="password" autocomplete="new-password" :minlength="8" hint="至少 8 個字元" :error="errors.password"/><FormField id="confirmPassword" v-model="form.confirmPassword" label="確認密碼" type="password" autocomplete="new-password" :minlength="8" hint="再次輸入密碼" :error="errors.confirmPassword"/></div><p class="form-alert" role="alert" aria-live="polite">{{ alert }}</p><button class="button button-primary auth-submit" type="submit" :disabled="loading">{{ loading ? '建立中…' : '建立免費帳號 →' }}</button></form><p class="auth-switch">已經有帳號？ <RouterLink to="/login">直接登入</RouterLink></p></AuthShell></template>
