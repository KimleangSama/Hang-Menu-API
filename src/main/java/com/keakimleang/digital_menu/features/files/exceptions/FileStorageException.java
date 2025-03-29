package com.keakimleang.digital_menu.features.files.exceptions;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
