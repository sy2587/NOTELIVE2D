<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const menuOpen = ref(false)
const previewTasks = [
  { title: '複習英文單字', meta: '25 分鐘 · 語言', time: '09:00', done: true },
  { title: '整理資料結構筆記', meta: '45 分鐘 · 程式設計', time: '14:30' },
  { title: '完成數學練習', meta: '30 分鐘 · 數學', time: '19:00' }
]
const features = [
  { number: '01', icon: 'note', title: '讓筆記更好找', copy: '依科目和標籤整理內容，快速回到上次的思考，不再翻遍散落的檔案。' },
  { number: '02', icon: 'calendar', title: '把目標化成今天', copy: '用清楚的小任務安排進度，完成一件、前進一步，維持可持續的節奏。' },
  { number: '03', icon: 'chart', title: '看見自己的累積', copy: '用簡潔的進度回顧掌握學習狀態，把努力變成看得見的成長。' }
]
async function logout() { await auth.logout(); router.push({ name: 'login' }) }
</script>

<template>
  <a class="skip-link" href="#main-content">跳至主要內容</a>
  <header class="site-header">
    <RouterLink class="wordmark" to="/" aria-label="Kumi Study 首頁"><span class="wordmark-mark" aria-hidden="true">K</span><span>Kumi Study</span></RouterLink>
    <nav id="site-navigation" class="site-nav" :class="{ 'is-open': menuOpen }" aria-label="主要導覽">
      <a href="#features" @click="menuOpen = false">功能特色</a><a href="#today" @click="menuOpen = false">學習節奏</a>
      <template v-if="auth.isAuthenticated"><span class="user-chip"><span class="user-dot" aria-hidden="true"></span>{{ auth.user.displayName }}</span><button class="nav-login" type="button" @click="logout">登出</button></template>
      <template v-else><RouterLink class="nav-login" to="/login">登入</RouterLink><RouterLink class="nav-action" to="/register">免費開始 <span aria-hidden="true">↗</span></RouterLink></template>
    </nav>
    <button class="menu-button" type="button" :aria-expanded="menuOpen" aria-controls="site-navigation" :aria-label="menuOpen ? '關閉導覽選單' : '開啟導覽選單'" @click="menuOpen = !menuOpen"><span></span><span></span></button>
  </header>

  <main id="main-content">
    <section class="hero" aria-labelledby="hero-title">
      <div class="hero-copy"><p class="eyebrow"><span></span>Study with intention</p><h1 id="hero-title">把複雜的學習，<em>整理成自己的節奏。</em></h1><p class="hero-intro">一個安靜、清楚的學習空間。把筆記、科目和每日任務放在一起，讓注意力回到真正重要的事。</p><div class="hero-actions"><RouterLink class="button button-primary" :to="auth.isAuthenticated ? '/dashboard' : '/register'">{{ auth.isAuthenticated ? '回到我的書桌' : '建立我的學習空間' }}<span aria-hidden="true">→</span></RouterLink><a class="text-link" href="#features">先看看怎麼使用</a></div><div class="trust-row" aria-label="產品特色"><span>不用信用卡</span><span>隨時可以開始</span><span>專注、沒有廣告</span></div></div>
      <div class="hero-visual"><div class="visual-orbit orbit-one" aria-hidden="true"></div><div class="visual-orbit orbit-two" aria-hidden="true"></div><div class="workspace-card" aria-label="Kumi Study 學習空間預覽"><div class="card-topbar"><div><small>MON · 18 SEP</small><strong>早安，今天想完成什麼？</strong></div><span class="avatar" aria-hidden="true">久</span></div><div class="focus-progress"><span>本週學習進度</span><strong>72%</strong><div aria-hidden="true"><i></i></div></div><div class="task-label"><span>今日安排</span><small>1 / 3 完成</small></div><div v-for="task in previewTasks" :key="task.title" class="preview-task" :class="{ done: task.done }"><span class="check" aria-hidden="true">{{ task.done ? '✓' : '' }}</span><div><strong>{{ task.title }}</strong><small>{{ task.meta }}</small></div><time>{{ task.time }}</time></div><p class="quote-chip"><span aria-hidden="true">✦</span> 今天的進步，也值得被看見。</p></div><div class="floating-note" aria-hidden="true"><span>連續學習</span><strong>8 天</strong></div></div>
    </section>

    <section class="principle-strip" aria-label="Kumi Study 原則"><p>少一點切換</p><span></span><p>多一點理解</p><span></span><p>讓進步看得見</p></section>

    <section id="features" class="features" aria-labelledby="features-title">
      <div class="section-heading"><div><p class="eyebrow eyebrow-light"><span></span>Your study desk</p><p class="section-kicker">學習不需要更多工具，<br>需要的是更清楚的下一步。</p></div><h2 id="features-title">需要的功能剛剛好，<br>不讓工具喧賓奪主。</h2></div>
      <div class="feature-grid"><article v-for="feature in features" :key="feature.number" class="feature-card"><span class="feature-number">{{ feature.number }}</span><svg v-if="feature.icon === 'note'" class="feature-icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M5 4.5h11a3 3 0 0 1 3 3v12H8a3 3 0 0 1-3-3v-12Z"/><path d="M8 19.5a3 3 0 0 1 3-3h8M9 8h6M9 11.5h5"/></svg><svg v-else-if="feature.icon === 'calendar'" class="feature-icon" viewBox="0 0 24 24" aria-hidden="true"><rect x="4" y="5" width="16" height="15" rx="2"/><path d="M8 3v4M16 3v4M4 10h16M8 14h3M8 17h7"/></svg><svg v-else class="feature-icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M5 19V9M12 19V5M19 19v-7M3 19h18"/></svg><h3>{{ feature.title }}</h3><p>{{ feature.copy }}</p><span class="card-arrow" aria-hidden="true">↗</span></article></div>
    </section>

    <section id="today" class="today" aria-labelledby="today-title"><div class="today-copy"><p class="eyebrow"><span></span>A gentler rhythm</p><h2 id="today-title">一次專心一件事，<em>就已經很好。</em></h2><p class="section-copy">把大目標拆成今天做得到的小事。清楚的優先順序，讓你不必把力氣花在決定下一步。</p><RouterLink class="text-link" to="/register">開始安排今天 <span aria-hidden="true">→</span></RouterLink></div><ol class="task-list"><li v-for="(task, index) in ['複習課堂重點','完成今日練習','整理一則新筆記']" :key="task"><span>0{{ index + 1 }}</span><div><h3>{{ task }}</h3><p>{{ ['閱讀筆記','專注練習','回顧歸納'][index] }} · 約 {{ 15 + index * 10 }} 分鐘</p></div><svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="8"/><path d="m9 12 2 2 4-5"/></svg></li></ol></section>

    <section class="closing"><p class="eyebrow eyebrow-light"><span></span>Start small, grow steadily</p><h2>今天寫下的每一頁，<br>都會成為明天的底氣。</h2><p>為自己留一個安靜學習的位置。</p><RouterLink class="button button-light" to="/register">免費建立帳號 <span aria-hidden="true">→</span></RouterLink></section>
  </main>
  <footer class="site-footer"><span class="wordmark">Kumi Study</span><p>為專注與長期學習而設計。</p><span>© 2026 Kumi Study</span></footer>
</template>
