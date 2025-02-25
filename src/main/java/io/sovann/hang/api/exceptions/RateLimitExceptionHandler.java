package io.sovann.hang.api.exceptions;

import io.sovann.hang.api.features.commons.payloads.*;
import jakarta.servlet.http.*;
import org.slf4j.*;
import org.springframework.core.*;
import org.springframework.core.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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