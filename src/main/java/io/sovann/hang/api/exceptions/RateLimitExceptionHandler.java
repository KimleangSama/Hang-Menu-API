package io.sovann.hang.api.exceptions;

import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RateLimitExceptionHandler.class);

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<BaseResponse<?>> handleInvalidFieldsInValidJson(final RateLimitException rateLimitException, final HttpServletRequest request) {
        final BaseResponse<?> apiErrorMessage = rateLimitException.toApiErrorMessage(request.getRequestURI());
        logIncomingCallException(rateLimitException, apiErrorMessage);
        return new ResponseEntity<>(apiErrorMessage, HttpStatus.TOO_MANY_REQUESTS);
    }

    private static void logIncomingCallException(final RateLimitException rateLimitException, final BaseResponse<?> apiErrorMessage) {
        LOG.error("{}: {}", apiErrorMessage.getTimestamp(), rateLimitException.getMessage(), rateLimitException);
    }
}