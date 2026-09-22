export function notifyLive2D(message, expression = true) {
  window.dispatchEvent(new CustomEvent('kumi:live2d', { detail: { message, expression } }))
}
