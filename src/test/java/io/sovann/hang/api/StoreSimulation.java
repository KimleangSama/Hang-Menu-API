package io.sovann.hang.api;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.sovann.hang.api.services.AuthSimulationService;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


public class StoreSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:10000")
            .acceptHeader("application/json")
            .header("Content-Type", "application/json");

    ScenarioBuilder scn = scenario("Store Simulation")
            .exec(AuthSimulationService.login()
                    .check(status().is(200)));

    {
        setUp(
                scn.injectOpen(injection())
        ).protocols(httpProtocol);
    }

    private OpenInjectionStep.RampRate.RampRateOpenInjectionStep injection() {
        int total = 1000;
        double userRampUpPerInterval = 10;
        double rampUpIntervalInSeconds = 30;

        int rampUptimeSeconds = 300;
        int duration = 30;
        return rampUsersPerSec(userRampUpPerInterval / (rampUpIntervalInSeconds)).to(total)
                .during(Duration.ofSeconds(rampUptimeSeconds + duration));
    }
}
