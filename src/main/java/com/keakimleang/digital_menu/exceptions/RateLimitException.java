package com.keakimleang.digital_menu.exceptions;


import com.keakimleang.digital_menu.commons.payloads.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitException extends RuntimeException {

    public RateLimitException(final String message) {
        super(message);
    }

    public BaseResponse<?> toApiErrorMessage(final String path) {
        return BaseResponse.exception()
                .setError(getMessage() + " - " + path)
                .setStatus(BaseResponse.Status.TOO_MANY_REQUESTS)
                .setStatusCode(429);
    }
}
