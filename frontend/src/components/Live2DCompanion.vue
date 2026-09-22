<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const modelUrl = import.meta.env.VITE_LIVE2D_MODEL_URL || '/live2d/model/model3.json'
const coreUrl = import.meta.env.VITE_LIVE2D_CORE_URL || '/live2d/live2dcubismcore.min.js'
const panel = ref(null)
const canvas = ref(null)
const stage = ref(null)
const collapsed = ref(localStorage.getItem('kumi-live2d-collapsed') === 'true')
const status = ref('loading')
const message = ref('歡迎回來，今天想先完成什麼？')
const position = ref(readPosition())
const modelMetrics = ref('')
let app = null
let model = null
let resizeObserver = null
let dragState = null
let messageTimer = null

const statusText = computed(() => ({
  loading: '正在召喚學習夥伴…',
  ready: 'Live2D 學習夥伴已就緒',
  missing: '尚未放入 Live2D 模型',
  error: 'Live2D 模型載入失敗'
})[status.value])

const routeMessages = {
  dashboard: '把想法貼下來，再拆成一個個小任務吧。',
  notes: '記得用資料夾和標籤，讓筆記之後也找得到。',
  tasks: '先完成一件最重要的事，今天就有進展。'
}

watch(() => route.name, name => {
  if (routeMessages[name]) showMessage(routeMessages[name])
})

onMounted(() => {
  window.addEventListener('kumi:live2d', handleNotification)
  if (!collapsed.value) loadModel()
})

onBeforeUnmount(() => {
  window.removeEventListener('kumi:live2d', handleNotification)
  stopDragging()
  destroyModel()
  window.clearTimeout(messageTimer)
})

function readPosition() {
  try {
    const saved = JSON.parse(localStorage.getItem('kumi-live2d-position'))
    if (Number.isFinite(saved?.right) && Number.isFinite(saved?.bottom)) return saved
  } catch { /* use the default position */ }
  return { right: 24, bottom: 24 }
}

function handleNotification(event) {
  showMessage(event.detail?.message || '很棒，繼續保持這個節奏！')
  if (event.detail?.expression && model?.expression) model.expression().catch(() => {})
}

function showMessage(text) {
  message.value = text
  window.clearTimeout(messageTimer)
  messageTimer = window.setTimeout(() => {
    message.value = routeMessages[route.name] || '慢慢來，專心做好眼前的一件事。'
  }, 6500)
}

async function expand() {
  collapsed.value = false
  localStorage.setItem('kumi-live2d-collapsed', 'false')
  await nextTick()
  if (!app) loadModel()
}

function collapse() {
  collapsed.value = true
  localStorage.setItem('kumi-live2d-collapsed', 'true')
}

async function loadModel() {
  if (!canvas.value || app || status.value === 'loading' && model) return
  status.value = 'loading'
  try {
    const response = await fetch(modelUrl, { cache: 'no-store' })
    if (!response.ok) throw new Error('MODEL_NOT_FOUND')
    let manifest
    try { manifest = await response.json() }
    catch { throw new Error('MODEL_NOT_FOUND') }
    if (!manifest?.FileReferences?.Moc) throw new Error('MODEL_NOT_FOUND')
    await loadCore()

    const PIXI = await import('pixi.js')
    window.PIXI = PIXI
    const { Live2DModel } = await import('pixi-live2d-display/lib/cubism4')
    app = new PIXI.Application({ view: canvas.value, resizeTo: stage.value, transparent: true, antialias: true, autoStart: true })
    Live2DModel.registerTicker(PIXI.Ticker)
    model = await Live2DModel.from(modelUrl, { autoInteract: false })
    applyCubismCoreCompatibility(model)
    model.interactive = true
    model.visible = true
    model.alpha = 1
    app.stage.addChild(model)
    fitModel()
    requestAnimationFrame(fitModel)
    window.setTimeout(fitModel, 250)
    resizeObserver = new ResizeObserver(fitModel)
    resizeObserver.observe(stage.value)
    stage.value.addEventListener('pointermove', followPointer)
    stage.value.addEventListener('pointerdown', tapModel)
    status.value = 'ready'
  } catch (error) {
    destroyModel()
    status.value = error.message === 'MODEL_NOT_FOUND' ? 'missing' : 'error'
  }
}

/**
 * pixi-live2d-display 0.3 uses the Cubism 4 render-order property. Cubism
 * Core 6 exposes the same data through getRenderOrders(), so bridge that one
 * API at the model boundary instead of modifying the installed dependency.
 */
function applyCubismCoreCompatibility(live2dModel) {
  const cubismModel = live2dModel?.internalModel?.coreModel
  const nativeModel = cubismModel?._model
  if (!cubismModel || typeof nativeModel?.getRenderOrders !== 'function') return
  cubismModel.getDrawableRenderOrders = () => nativeModel.getRenderOrders()
}

function loadCore() {
  if (window.Live2DCubismCore) return Promise.resolve()
  return new Promise((resolve, reject) => {
    const existing = document.querySelector('script[data-kumi-live2d-core]')
    if (existing) {
      existing.addEventListener('load', resolve, { once: true })
      existing.addEventListener('error', reject, { once: true })
      return
    }
    const script = document.createElement('script')
    script.src = coreUrl
    script.async = true
    script.dataset.kumiLive2dCore = 'true'
    script.onload = resolve
    script.onerror = reject
    document.head.appendChild(script)
  })
}

function fitModel() {
  if (!model || !stage.value) return
  const width = stage.value.clientWidth
  const height = stage.value.clientHeight
  if (width <= 0 || height <= 0) return
  app?.renderer.resize(width, height)
  const bounds = drawableBounds()
  if (!bounds) {
    status.value = 'error'
    modelMetrics.value = 'No drawable bounds'
    return
  }
  model.scale.set(1)
  // Frame the character as an upper-body portrait: the top half of the
  // drawable bounds fills the stage and the remainder is cropped below it.
  const visibleBodyRatio = 0.42
  const topPadding = height * 0.04
  const availableHeight = height * 0.92
  const scale = availableHeight / (bounds.height * visibleBodyRatio)
  if (!Number.isFinite(scale) || scale <= 0) {
    status.value = 'error'
    modelMetrics.value = `Invalid scale: ${scale}`
    return
  }
  model.pivot.set(bounds.centerX, bounds.minY)
  model.scale.set(scale)
  model.x = width / 2
  model.y = topPadding
  modelMetrics.value = `${Math.round(bounds.width)}×${Math.round(bounds.height)} @ ${scale.toFixed(3)}`
}

function drawableBounds() {
  const internal = model?.internalModel
  if (!internal?.getDrawableIDs || !internal?.getDrawableVertices) return null
  let minX = Infinity, minY = Infinity, maxX = -Infinity, maxY = -Infinity
  for (const id of internal.getDrawableIDs()) {
    const vertices = internal.getDrawableVertices(id)
    for (let index = 0; index < vertices.length; index += 2) {
      const x = vertices[index]
      const y = vertices[index + 1]
      if (!Number.isFinite(x) || !Number.isFinite(y)) continue
      minX = Math.min(minX, x); maxX = Math.max(maxX, x)
      minY = Math.min(minY, y); maxY = Math.max(maxY, y)
    }
  }
  if (![minX, minY, maxX, maxY].every(Number.isFinite) || maxX <= minX || maxY <= minY) return null
  return { minX, minY, maxX, maxY, width: maxX - minX, height: maxY - minY, centerX: (minX + maxX) / 2 }
}

function followPointer(event) {
  if (!model) return
  const rect = stage.value.getBoundingClientRect()
  model.focus(event.clientX - rect.left, event.clientY - rect.top)
}

function tapModel(event) {
  if (!model) return
  const rect = stage.value.getBoundingClientRect()
  model.tap(event.clientX - rect.left, event.clientY - rect.top)
  showMessage('我在這裡！一起把今天的目標完成吧。')
}

function destroyModel() {
  resizeObserver?.disconnect()
  resizeObserver = null
  stage.value?.removeEventListener('pointermove', followPointer)
  stage.value?.removeEventListener('pointerdown', tapModel)
  if (app) app.destroy(true, { children: true, texture: false, baseTexture: false })
  app = null
  model = null
}

function retry() {
  destroyModel()
  loadModel()
}

function cycleTip() {
  const tips = [
    '試著把大任務拆成 20 分鐘能完成的小步驟。',
    '先完成最重要的一件事，再處理其他待辦。',
    '學習後留下一句摘要，之後複習會輕鬆很多。'
  ]
  showMessage(tips[Math.floor(Math.random() * tips.length)])
}

function startDragging(event) {
  if (event.button !== 0) return
  dragState = { x: event.clientX, y: event.clientY, right: position.value.right, bottom: position.value.bottom }
  window.addEventListener('pointermove', drag)
  window.addEventListener('pointerup', stopDragging, { once: true })
}

function drag(event) {
  if (!dragState || !panel.value) return
  const maxRight = Math.max(8, window.innerWidth - panel.value.offsetWidth - 8)
  const maxBottom = Math.max(8, window.innerHeight - panel.value.offsetHeight - 8)
  position.value = {
    right: Math.min(maxRight, Math.max(8, dragState.right - (event.clientX - dragState.x))),
    bottom: Math.min(maxBottom, Math.max(8, dragState.bottom - (event.clientY - dragState.y)))
  }
}

function stopDragging() {
  if (dragState) localStorage.setItem('kumi-live2d-position', JSON.stringify(position.value))
  dragState = null
  window.removeEventListener('pointermove', drag)
}
</script>

<template>
  <button v-if="collapsed" class="live2d-launcher" type="button" aria-label="開啟 Live2D 學習夥伴" @click="expand"><span>K</span><small>Live2D</small></button>
  <aside v-else ref="panel" class="live2d-companion" :style="{ right: `${position.right}px`, bottom: `${position.bottom}px` }" :data-live2d-status="status" :data-model-metrics="modelMetrics" aria-label="Live2D 學習夥伴">
    <header class="live2d-handle" title="拖曳移動" @pointerdown="startDragging"><span><i aria-hidden="true"></i>Kumi</span><button type="button" aria-label="收起 Live2D 學習夥伴" @pointerdown.stop @click="collapse">−</button></header>
    <div ref="stage" class="live2d-stage" :class="`is-${status}`">
      <canvas ref="canvas" aria-label="Live2D 角色"></canvas>
      <button v-if="status !== 'ready'" class="live2d-fallback" type="button" @click="cycleTip"><span aria-hidden="true">K</span><strong>{{ statusText }}</strong><small v-if="status === 'missing'">放入模型後即會自動啟用</small></button>
      <span v-if="status === 'loading'" class="live2d-loader" aria-hidden="true"></span>
    </div>
    <div class="live2d-speech" aria-live="polite"><span aria-hidden="true">✦</span><p>{{ message }}</p></div>
    <footer><span>{{ statusText }}</span><button v-if="status === 'missing' || status === 'error'" type="button" @click="retry">重新載入</button></footer>
  </aside>
</template>
