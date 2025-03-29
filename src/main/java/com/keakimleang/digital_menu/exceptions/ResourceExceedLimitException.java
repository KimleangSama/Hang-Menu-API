package com.keakimleang.digital_menu.exceptions;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
