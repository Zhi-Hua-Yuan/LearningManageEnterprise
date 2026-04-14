package com.spt.learningmanage.exception;

import com.spt.learningmanage.common.BaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ForbiddenResponseContractTest {

    @Test
    void shouldReturnForbiddenErrorPayloadForForbiddenException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        BaseResponse<Void> response = handler.handleForbiddenException(new ForbiddenException("内部细节不应透传"));

        assertEquals(ErrorCode.FORBIDDEN_ERROR.getCode(), response.getCode());
        assertEquals(ErrorCode.FORBIDDEN_ERROR.getMessage(), response.getMessage());
    }

    @Test
    void shouldDeclareHttp403OnForbiddenHandlerMethod() throws NoSuchMethodException {
        Method method = GlobalExceptionHandler.class.getMethod("handleForbiddenException", ForbiddenException.class);

        ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);

        assertNotNull(responseStatus);
        assertEquals(HttpStatus.FORBIDDEN, responseStatus.value());
    }
}

