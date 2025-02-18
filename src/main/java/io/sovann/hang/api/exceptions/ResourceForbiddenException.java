package io.sovann.hang.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceForbiddenException extends RuntimeException {
    private final String username;
    private final transient Class<?> resource;

    public ResourceForbiddenException(String username, Class<?> resource) {
        super("User: " + username + " is not allowed to access this resource: " + resource.getName() + ".");
        this.username = username;
        this.resource = resource;
    }
}
