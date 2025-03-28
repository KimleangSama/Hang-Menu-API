package com.keakimleang.digital_menu.api.services;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.javaapi.http.*;

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
