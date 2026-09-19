package com.kumistudy.dashboard;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    public void getDashboard() {
        // TODO: 組合今日待辦、最近筆記、即將到期任務與統計摘要。
        // TODO: 優先呼叫各功能的 service，避免 Dashboard 直接操作多個 Repository。
    }
}
