package io.sovann.hang.api.services;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class MenuSimulationService {
    public static HttpRequestActionBuilder getMenuOfStore() {
        return http("Get Menu Of Store Request")
                .get("/api/v1/menus/of-store/be168fed-8d3a-4241-ba6d-61366ccdda62/all/with")
                .check(status().is(200));
    }
}
