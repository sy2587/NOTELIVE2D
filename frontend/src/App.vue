<script setup>
import { computed } from 'vue'
import { RouterView } from 'vue-router'
import { useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'
import Live2DCompanion from './components/Live2DCompanion.vue'

const auth = useAuthStore()
const route = useRoute()
const showCompanion = computed(() => auth.isAuthenticated || (import.meta.env.DEV && route.query.live2dPreview === 'true'))
</script>

<template><RouterView /><Live2DCompanion v-if="showCompanion" /></template>
