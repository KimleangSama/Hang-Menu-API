package io.sovann.hang.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ResourceExceedLimitException extends RuntimeException {
    private final String resourceName;
    private final String resourceContainer;
    private final int limit;

    public ResourceExceedLimitException(String resourceName, String resourceContainer, int limit) {
        super(String.format("%s in a %s cannot exceed limit of %s", resourceName, resourceContainer, limit));
        this.resourceName = resourceName;
        this.resourceContainer = resourceContainer;
        this.limit = limit;
    }

}
