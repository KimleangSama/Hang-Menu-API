package io.sovann.hang.api.commons.payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse<T> implements Serializable {
    private Status status;
    private int statusCode;
    private transient T payload;
    private transient Object error;
    private boolean success = false;
    private Instant timestamp = Instant.now();
    private transient PageInfo page;

    public static <T> BaseResponse<T> badRequest() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.BAD_REQUEST);
        response.setStatusCode(400);
        return response;
    }

    public static <T> BaseResponse<T> notAcceptable() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.NOT_ACCEPTABLE);
        response.setStatusCode(406);
        return response;
    }

    public static <T> BaseResponse<T> ok() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.OK);
        response.setStatusCode(200);
        response.setSuccess(true);
        return response;
    }

    public static <T> BaseResponse<T> created() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.CREATED);
        response.setSuccess(true);
        response.setStatusCode(201);
        return response;
    }

    public static <T> BaseResponse<T> unauthorized() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.UNAUTHORIZED);
        response.setStatusCode(401);
        return response;
    }

    public static <T> BaseResponse<T> wrongCredentials() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.WRONG_CREDENTIALS);
        response.setStatusCode(401);
        return response;
    }

    public static <T> BaseResponse<T> accessDenied() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.ACCESS_DENIED);
        response.setStatusCode(403);
        return response;
    }

    public static <T> BaseResponse<T> exception() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.EXCEPTION);
        response.setStatusCode(500);
        return response;
    }

    public static <T> BaseResponse<T> invalidToken() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.VALIDATION_EXCEPTION);
        response.setStatusCode(400);
        return response;
    }

    public static <T> BaseResponse<T> notFound() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.NOT_FOUND);
        response.setStatusCode(404);
        return response;
    }

    public static <T> BaseResponse<T> generationNotAvailable() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.GENERATION_NOT_AVAILABLE);
        response.setStatusCode(503);
        return response;
    }

    public static <T> BaseResponse<T> duplicateEntity() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.DUPLICATE_ENTITY);
        response.setStatusCode(409);
        return response;
    }

    public static <T> BaseResponse<T> tooManyRequests() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.TOO_MANY_REQUESTS);
        response.setStatusCode(429);
        return response;
    }

    public static <T> BaseResponse<T> expectedFailed() {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(Status.EXPECTED_FAILED);
        response.setStatusCode(417);
        return response;
    }

    public enum Status {
        GENERATION_NOT_AVAILABLE, NOT_ACCEPTABLE, OK, BAD_REQUEST, UNAUTHORIZED, VALIDATION_EXCEPTION, EXCEPTION,
        WRONG_CREDENTIALS, ACCESS_DENIED, NOT_FOUND, CREATED, DUPLICATE_ENTITY, TOO_MANY_REQUESTS,
        EXPECTED_FAILED
    }
}