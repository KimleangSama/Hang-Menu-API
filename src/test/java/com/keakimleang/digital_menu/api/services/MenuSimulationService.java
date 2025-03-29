package com.keakimleang.digital_menu.api.services;

import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.javaapi.http.*;

public class MenuSimulationService {
    public static HttpRequestActionBuilder getMenuOfStore() {
        return http("Get Menu Of Store Request")
                .get("/api/v1/menus/of-store/be168fed-8d3a-4241-ba6d-61366ccdda62/all/with")
                .check(status().is(200));
    }
}
