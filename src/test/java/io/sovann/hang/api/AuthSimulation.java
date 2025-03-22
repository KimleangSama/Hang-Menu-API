package io.sovann.hang.api;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.sovann.hang.api.services.AuthSimulationService;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


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
