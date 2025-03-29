package com.keakimleang.digital_menu.api.services;

import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.javaapi.http.*;

public class StoreSimulationService {
    public static HttpRequestActionBuilder getStoreBySlug() {
        return http("Get Store Request")
                .get("/api/v1/stores/kimleangrestaurant/get")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiaXNzIjoiand0X2lzc3VlciIsImlhdCI6MTc0MjYwNDQ5MCwiZXhwIjoxNzQzODE0MDkwfQ.T8KNNL-Gcy9O4lXJbgyJxHpBCLXGKwheN5c6cgA9oFc")
                .check(status().is(200));
    }
}
