package com.keakimleang.digital_menu.api;

import com.keakimleang.digital_menu.api.services.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import io.gatling.javaapi.core.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.javaapi.http.*;
import java.time.*;


public class AuthSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:10000")
            .acceptHeader("application/json")
            .header("Content-Type", "application/json");

    ScenarioBuilder scn = scenario("Login Simulation")
            .exec(AuthSimulationService.login()
                    .check(status().is(200)));

    {
        setUp(
                scn.injectOpen(rampUsersPerSec(1).to(100).during(Duration.ofMinutes(1)))
        ).protocols(httpProtocol);
    }

}
