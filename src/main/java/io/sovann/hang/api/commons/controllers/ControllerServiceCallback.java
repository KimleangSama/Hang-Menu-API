package io.sovann.hang.api.commons.controllers;

import io.sovann.hang.api.commons.payloads.BaseResponse;
import io.sovann.hang.api.commons.payloads.PageInfo;
import io.sovann.hang.api.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ControllerServiceCallback {
    private static final String LOG_ERROR = "{}: {}";

    public <T> BaseResponse<T> execute(ServiceOperation<T> operation, String errorMessage, PageInfo page) {
        try {
            T result = operation.execute();
            return BaseResponse.<T>ok().setPayload(result).setPage(page);
        } catch (ResourceNotFoundException e) {
            log.error(LOG_ERROR, errorMessage, e.getMessage(), e);
            return BaseResponse.<T>notFound().setError(e.getMessage());
        } catch (ResourceForbiddenException e) {
            log.error(LOG_ERROR, errorMessage, e.getMessage(), e);
            return BaseResponse.<T>accessDenied().setError(e.getMessage());
        } catch (ResourceDeletedException | ConstraintViolationException e) {
            log.error(LOG_ERROR, errorMessage, e.getMessage(), e);
            return BaseResponse.<T>badRequest().setError(e.getMessage());
        } catch (ResourceExceedLimitException e) {
            log.error(LOG_ERROR, errorMessage, e.getMessage(), e);
            return BaseResponse.<T>expectedFailed().setError(e.getMessage());
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
