package io.sovann.hang.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.UUID;

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
