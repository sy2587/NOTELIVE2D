package com.kumistudy.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    // TODO: 注入 UserRepository、PasswordEncoder 與目前登入者所需的元件。
    // TODO: 註冊時驗證 username/email 唯一性，再使用 PasswordEncoder 加密密碼。
    @Transactional
    public void register() {
        // TODO: 接收註冊 DTO，建立 User，賦予 USER 角色並儲存。
    }

    // TODO: Session 登入由 Spring Security 處理；這裡負責帳號狀態與登入資料驗證。
    public void login() {
        // TODO: 驗證帳號存在、未停用，並建立登入狀態。
    }

    public void logout() {
        // TODO: 清除 Session，並讓前端回到登入頁。
    }
}
