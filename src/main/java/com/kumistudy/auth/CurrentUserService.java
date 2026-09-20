package com.kumistudy.auth;

import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    public Long requireUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object userId = session == null ? null : session.getAttribute("loginUserId");
        if (userId instanceof Long id) {
            return id;
        }
        if (userId instanceof Number number) {
            return number.longValue();
        }
        throw new ApiException(ErrorCode.UNAUTHORIZED);
    }
}
