package com.keakimleang.digital_menu.exceptions;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String fieldNameValue;

    public ResourceNotFoundException(String resourceName, String fieldNameValue) {
        super(String.format("%s not found with %s", resourceName, fieldNameValue));
        this.resourceName = resourceName;
        this.fieldNameValue = fieldNameValue;
    }

}

