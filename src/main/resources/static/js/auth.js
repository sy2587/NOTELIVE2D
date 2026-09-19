// TODO: 負責登入狀態檢查、登入、註冊、登出與頁面導向。

async function loadCurrentUser() {
    // TODO: 呼叫 GET /api/auth/me；未登入時導向 login.html。
}

async function submitLogin(form) {
    // TODO: 讀取表單、呼叫 POST /api/auth/login，成功後導向 index.html。
    // TODO: 不要把密碼寫入 localStorage 或 console。
}

async function submitRegister(form) {
    // TODO: 先做前端基本驗證，後端驗證仍然是必要的。
}
