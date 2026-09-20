package com.kumistudy.auth;

import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserServiceTest {

    private final CurrentUserService currentUserService = new CurrentUserService();

    @Test
    void requireUserId_shouldReadIdFromSession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true).setAttribute("loginUserId", 42L);

        assertEquals(42L, currentUserService.requireUserId(request));
    }

    @Test
    void requireUserId_shouldRejectMissingSession() {
        ApiException exception = assertThrows(
                ApiException.class,
                () -> currentUserService.requireUserId(new MockHttpServletRequest())
        );

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }
}
