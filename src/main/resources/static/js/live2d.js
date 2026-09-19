// TODO: 封裝 Live2D 初始化，避免角色載入錯誤影響主要學習功能。

async function loadLive2DModel(modelUrl) {
    // TODO: 載入 model3.json 與必要的模型資源。
    // TODO: physics3.json 是可選檔案；載入失敗時記錄警告並繼續顯示模型。
    // TODO: 將拖曳、縮放、視線跟隨與顯示/隱藏控制分開實作。
}

function handleLive2DError(error) {
    // TODO: 顯示非阻斷式提示，不能讓整個 dashboard 變成空白頁。
    console.warn("Live2D 載入失敗", error);
}
