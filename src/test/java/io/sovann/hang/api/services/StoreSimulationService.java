package io.sovann.hang.api.services;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class StoreSimulationService {
    public static HttpRequestActionBuilder getStoreBySlug() {
        return http("Get Store Request")
                .get("/api/v1/stores/kimleangrestaurant/get")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiaXNzIjoiand0X2lzc3VlciIsImlhdCI6MTc0MjYwNDQ5MCwiZXhwIjoxNzQzODE0MDkwfQ.T8KNNL-Gcy9O4lXJbgyJxHpBCLXGKwheN5c6cgA9oFc")
                .check(status().is(200));
    }
}
