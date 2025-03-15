package io.sovann.hang.api.services;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class AuthSimulationService {
    public static HttpRequestActionBuilder login() {
        return http("Login `superadmin` Request")
                .post("/api/v1/auth/login")
                .body(StringBody(
                        "{\"username\":\"superadmin\",\"password\":\"superadmin@_@\"}"
                ))
                .check(status().is(200));
    }
}
