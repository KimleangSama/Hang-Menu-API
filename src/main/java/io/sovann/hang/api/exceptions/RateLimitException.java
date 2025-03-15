package io.sovann.hang.api.exceptions;


import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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
