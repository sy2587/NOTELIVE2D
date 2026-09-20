<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const menuOpen = ref(false)

async function logout() {
  await auth.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <a class="skip-link" href="#main-content">跳至主要內容</a>
  <header class="site-header">
    <RouterLink class="wordmark" to="/" aria-label="Kumi Study 首頁"><span class="wordmark-mark">K</span><span>Kumi Study</span></RouterLink>
    <nav id="site-navigation" class="site-nav" :class="{ 'is-open': menuOpen }" aria-label="主要導覽">
      <a href="#features" @click="menuOpen = false">功能</a><a href="#today" @click="menuOpen = false">今日焦點</a>
      <template v-if="auth.isAuthenticated"><span class="welcome">嗨，{{ auth.user.displayName }}</span><button class="nav-login" type="button" @click="logout">登出</button></template>
      <template v-else><RouterLink class="nav-login" to="/login">登入</RouterLink><RouterLink class="nav-action" to="/register">免費開始</RouterLink></template>
    </nav>
    <button class="menu-button" type="button" :aria-expanded="menuOpen" aria-controls="site-navigation" :aria-label="menuOpen ? '關閉導覽選單' : '開啟導覽選單'" @click="menuOpen = !menuOpen"><span></span><span></span></button>
  </header>

  <main id="main-content">
    <section class="hero">
      <div class="hero-copy"><p class="eyebrow"><span></span>專注學習，從容累積</p><h1>把複雜的學習，<em>整理成自己的節奏。</em></h1><p class="hero-intro">筆記、科目與每日任務集中在一處。少一點切換與干擾，多一點真正理解和持續前進。</p><div class="hero-actions"><RouterLink class="button button-primary" :to="auth.isAuthenticated ? '/' : '/register'">{{ auth.isAuthenticated ? '進入學習空間' : '建立學習空間' }} <span>→</span></RouterLink><a class="text-link" href="#features">看看如何運作 ↓</a></div><ul class="hero-proof"><li>✓ 免費開始</li><li>✓ 無廣告干擾</li><li>✓ 適合各種科目</li></ul></div>
      <div class="workspace-card" aria-label="學習空間預覽"><div class="card-topbar"><div><small>TODAY</small><strong>今天，專心做好三件事</strong></div><span class="avatar">久</span></div><div class="focus-progress"><span>本週進度</span><strong>72%</strong><div><i></i></div></div><PreviewTask done title="複習英文單字" meta="25 分鐘 · 語言" time="09:00"/><PreviewTask title="整理資料結構筆記" meta="45 分鐘 · 程式設計" time="14:30"/><PreviewTask title="完成數學練習" meta="30 分鐘 · 數學" time="19:00"/><p class="quote-chip">✦ 今天的進步，也值得被看見。</p></div>
    </section>

    <section id="features" class="features"><div class="section-heading"><p class="eyebrow eyebrow-light"><span></span>你的學習桌</p><h2>需要的工具剛剛好，<br>不讓功能喧賓奪主。</h2></div><div class="feature-grid"><FeatureCard number="01" title="讓筆記更好找">依科目和標籤整理內容，快速回到上次的思考，不再翻遍散落的檔案。</FeatureCard><FeatureCard number="02" title="把目標化成今天">用清楚的小任務安排進度，完成一件、前進一步，維持可持續的節奏。</FeatureCard><FeatureCard number="03" title="看見自己的累積">用簡潔的進度回顧掌握學習狀態，把努力變成看得見的成長。</FeatureCard></div></section>
    <section id="today" class="today"><div><p class="eyebrow"><span></span>Today, in focus</p><h2>一次專心一件事，<em>就已經很好。</em></h2><p class="section-copy">清楚的優先順序，讓你不需要把力氣花在決定下一步。</p></div><ol class="task-list"><li v-for="(task, index) in ['複習課堂重點','完成今日練習','整理一則新筆記']" :key="task"><span>0{{ index + 1 }}</span><div><h3>{{ task }}</h3><p>專注學習 · 約 {{ 15 + index * 10 }} 分鐘</p></div><b>○</b></li></ol></section>
    <section class="closing"><p class="eyebrow eyebrow-light"><span></span>你的節奏，由你定義</p><h2>今天寫下的每一頁，<br>都會成為明天的底氣。</h2><RouterLink class="button button-light" to="/register">免費建立帳號 →</RouterLink></section>
  </main>
  <footer class="site-footer"><span class="wordmark">Kumi Study</span><p>為專注與長期學習而設計。</p><span>© 2026 Kumi Study</span></footer>
</template>

<script>
const PreviewTask = { props: { title: String, meta: String, time: String, done: Boolean }, template: `<div class="preview-task" :class="{ done }"><span class="check">{{ done ? '✓' : '' }}</span><div><strong>{{ title }}</strong><small>{{ meta }}</small></div><time>{{ time }}</time></div>` }
const FeatureCard = { props: { number: String, title: String }, template: `<article class="feature-card"><span>{{ number }}</span><div class="feature-icon">◇</div><h3>{{ title }}</h3><p><slot /></p></article>` }
export default { components: { PreviewTask, FeatureCard } }
</script>
