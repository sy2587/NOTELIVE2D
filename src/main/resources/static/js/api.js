// TODO: 在這裡集中管理 Fetch API，讓頁面不要重複撰寫 URL、JSON 解析與錯誤處理。

const apiClient = {
    async request(url, options = {}) {
        // TODO: 加入 credentials: "same-origin"，讓 Session cookie 隨請求送出。
        // TODO: 統一設定 Content-Type，解析 ApiResponse，並處理 401/403/422/500。
        throw new Error("apiClient.request 尚未實作");
    }
};
