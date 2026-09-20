<script setup>
defineProps({ mode: { type: String, required: true } })
</script>

<template>
  <a class="skip-link" href="#auth-form">跳至表單</a>
  <RouterLink class="wordmark auth-home" to="/" aria-label="返回首頁"><span class="wordmark-mark" aria-hidden="true">K</span><span>Kumi Study</span></RouterLink>
  <main class="auth-main">
    <section class="auth-story"><div><p class="eyebrow eyebrow-light"><span></span>{{ mode === 'login' ? 'Welcome back' : 'Start your rhythm' }}</p><h1 v-if="mode === 'login'">回到你的空間，<em>繼續穩穩前進。</em></h1><h1 v-else>從今天開始，<em>累積自己的節奏。</em></h1><p>{{ mode === 'login' ? '每一次整理與回顧，都在替未來的自己留下清楚的路徑。' : '把散落的筆記與任務收進同一個空間，讓每次回來都知道下一步。' }}</p><ul class="auth-benefits"><li>筆記、任務與科目集中管理</li><li>每次回來都知道下一步</li><li>沒有廣告，保持專注</li></ul></div></section>
    <section class="auth-panel"><div class="auth-panel-inner"><slot /></div></section>
  </main>
</template>
