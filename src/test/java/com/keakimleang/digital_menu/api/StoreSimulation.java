package com.keakimleang.digital_menu.api;

import com.keakimleang.digital_menu.api.services.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import io.gatling.javaapi.core.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.javaapi.http.*;
import java.time.*;


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
