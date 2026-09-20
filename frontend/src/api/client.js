let csrf = null

async function getCsrfToken() {
  if (csrf) return csrf
  const response = await fetch('/api/auth/csrf', { credentials: 'include' })
  if (!response.ok) return null
  const payload = await response.json()
  csrf = payload.data
  return csrf
}

export async function primeCsrfToken() {
  return getCsrfToken()
}

export async function apiRequest(url, options = {}) {
  const headers = new Headers(options.headers || {})
  const hasBody = options.body !== undefined && options.body !== null
  const method = (options.method || 'GET').toUpperCase()
  if (hasBody && !(options.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  if (!['GET', 'HEAD', 'OPTIONS'].includes(method) && !['/api/auth/login', '/api/auth/register'].includes(url)) {
    const token = await getCsrfToken()
    if (token) headers.set(token.headerName, token.token)
  }

  const response = await fetch(url, { ...options, headers, credentials: 'include' })
  const contentType = response.headers.get('content-type') || ''
  const payload = contentType.includes('application/json') ? await response.json() : null

  if (!response.ok || payload?.success === false) {
    const fallbackMessage = response.status === 401
      ? '登入狀態已過期，請重新登入。'
      : response.status === 403
        ? '安全驗證失效，請重新登入後再試一次。'
        : `請求失敗（${response.status}）`
    const error = new Error(payload?.error?.message || fallbackMessage)
    error.status = response.status
    error.code = payload?.error?.code || 'REQUEST_FAILED'
    error.fieldErrors = payload?.error?.fieldErrors || []
    throw error
  }
  return payload?.data ?? payload
}

export function clearCsrfToken() {
  csrf = null
}
