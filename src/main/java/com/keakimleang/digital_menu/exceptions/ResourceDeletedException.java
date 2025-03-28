package com.keakimleang.digital_menu.exceptions;

import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceDeletedException extends RuntimeException {
    private final transient Class<?> resource;
    private final transient LocalDateTime deletedAt;
    private final transient UUID deletedBy;

    public ResourceDeletedException(Class<?> resource, LocalDateTime deletedAt, UUID deletedBy) {
        super(String.format("Resource %s has been deleted at %s by user with ID: %s", resource.getSimpleName(), deletedAt, deletedBy));
        this.resource = resource;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
    }
}
