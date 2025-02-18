package io.sovann.hang.api.features.commons.controllers;

import io.sovann.hang.api.exceptions.ResourceDeletedException;
import io.sovann.hang.api.exceptions.ResourceException;
import io.sovann.hang.api.exceptions.ResourceForbiddenException;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.commons.payloads.PageMeta;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ControllerServiceCallback {
    private static final String LOG_ERROR = "{}: {}";

    public <T> BaseResponse<T> execute(ServiceOperation<T> operation, String errorMessage, PageMeta page) {
        try {
            T result = operation.execute();
            return BaseResponse.<T>ok().setPayload(result).setMetadata(page);
        } catch (ResourceNotFoundException e) {
            log.error(LOG_ERROR, errorMessage, e.getMessage(), e);
            return BaseResponse.<T>notFound().setError(e.getMessage());
        } catch (ResourceForbiddenException e) {
            log.error(LOG_ERROR, errorMessage, e.getMessage(), e);
            return BaseResponse.<T>accessDenied().setError(e.getMessage());
        } catch (ResourceDeletedException | ConstraintViolationException e) {
            log.error(LOG_ERROR, errorMessage, e.getMessage(), e);
            return BaseResponse.<T>badRequest().setError(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            String getMessage = "Duplicate entity on unique field with details: " + e.getMessage();
            log.error(LOG_ERROR, errorMessage, getMessage, e);
            return BaseResponse.<T>duplicateEntity().setError(getMessage);
        } catch (Exception e) {
            log.error(LOG_ERROR, errorMessage, e.getMessage(), e);
            return BaseResponse.<T>exception().setError(e.getMessage());
        }
    }

    @FunctionalInterface
    public interface ServiceOperation<T> {
        T execute() throws ResourceException;
    }
}
