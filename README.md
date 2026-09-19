# Kumi Study

Kumi Study 是以 Java 21、Spring Boot 與 MySQL 建立的 Live2D 學習筆記平台。

第一版採用 Spring Boot 同時提供 REST API 與原生 HTML/CSS/JavaScript 前端，登入驗證使用 Spring Security Session。React、JWT、AI、WebSocket 與進階學習功能會在 MVP 完成後評估。

## 技術

- Java 21
- Spring Boot
- Spring Web / Spring Data JPA / Spring Security
- MySQL 8.4
- Flyway
- Maven
- HTML / CSS / JavaScript / Fetch API

## 啟動 MySQL

```powershell
docker compose up -d mysql
```

## 啟動應用程式

需要設定以下環境變數，或使用 `application.yml` 的本機預設值：

```text
DB_URL=jdbc:mysql://localhost:3306/kumi_study?useSSL=false&serverTimezone=Asia/Taipei&allowPublicKeyRetrieval=true
DB_USERNAME=kumi
DB_PASSWORD=change-me
```

```powershell
mvnw.cmd spring-boot:run
```

啟動後：

- 網站：http://localhost:8080
- Swagger：http://localhost:8080/swagger-ui.html
- OpenAPI：http://localhost:8080/api-docs

## 專案原則

- 使用 Flyway 管理資料庫 schema，不使用 Hibernate 自動建表。
- 所有使用者資料都必須依目前登入者隔離。
- 密碼只儲存 BCrypt hash，不儲存明文。
- 敏感設定使用環境變數，不提交到 Git。
- Live2D 的 `physics3.json` 為可選資源，缺少時模型仍需正常載入。

## 自行實作順序

目前的 Java 與前端檔案是學習用骨架。每個待完成位置都以 `TODO` 註明責任與驗收方向，請依以下順序實作：

1. 完成 `auth`：User 欄位、註冊 DTO、BCrypt、Session 登入與登出。
2. 完成 `common`：統一成功回應、錯誤回應、驗證錯誤與例外處理。
3. 完成 `subject`：先做列表與 CRUD，再加入 owner 權限檢查。
4. 完成 `tag`：建立標籤及筆記標籤關聯。
5. 完成 `note`：純文字 CRUD、搜尋、分頁、收藏與軟刪除。
6. 完成 `task`：今日待辦、完成狀態與逾期判斷。
7. 完成 `dashboard`：組合最近筆記與今日待辦，不把查詢邏輯塞進 Controller。
8. 最後串接前端 Fetch API，再加入 Live2D，確保角色錯誤不影響主要頁面。

每完成一個模組，至少補上：

- Service 的成功與失敗測試
- 未登入或跨使用者存取測試
- Controller 的驗證錯誤測試
- 對應 API 的 Swagger 說明

## 骨架目錄

```text
src/main/java/com/kumistudy
├─ auth         帳號、密碼與 Session
├─ subject      學期科目管理
├─ note         筆記與搜尋
├─ tag          標籤
├─ task         待辦事項
├─ dashboard    首頁摘要
├─ common       API 回應與例外處理
└─ config       Spring Security 與 OpenAPI 設定

src/main/resources/static
├─ css          全站樣式
└─ js           各頁面資料流與 Live2D 載入
```
