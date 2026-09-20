export async function apiRequest(url, options = {}) {
  const headers = new Headers(options.headers || {})
  const hasBody = options.body !== undefined && options.body !== null
  if (hasBody && !(options.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const response = await fetch(url, { ...options, headers, credentials: 'include' })
  const contentType = response.headers.get('content-type') || ''
  const payload = contentType.includes('application/json') ? await response.json() : null

  if (!response.ok || payload?.success === false) {
    const error = new Error(payload?.error?.message || `請求失敗（${response.status}）`)
    error.status = response.status
    error.code = payload?.error?.code || 'REQUEST_FAILED'
    error.fieldErrors = payload?.error?.fieldErrors || []
    throw error
  }
  return payload?.data ?? payload
}
