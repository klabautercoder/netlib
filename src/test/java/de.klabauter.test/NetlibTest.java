package de.klabauter.test;

import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.created;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;

/**
 * General Tests.
 *
 * First Mock some Services for testing. After that use the test Netlib Implementations
 * to talk to this services. Profit...
 *
 * @see <a href="https://specto.io/blog/2017/1/4/stubbing-http-apis-and-microservices-with-the-hoverfly-java-dsl/">Hoverfly</a>
 */
class NetlibTest {

    // Example of creating Hoverfly Test Services
    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode(
            SimulationSource.dsl(
                service("www.booking-service.com")
                    .get("/api/bookings/1")
                    .willReturn(success(json(new DummyObject())))
                    .post("/api/bookings")
                    .body(json(new DummyObject()))
                    .willReturn(created("http://www.booking-service.com/api/bookings/1")),
                service("www.payment-service.com")
                    .get("/api/payments/1")
                    .willReturn(success("{\"amount\": \"1.25\"\"}", "application/json"))
            )
    );

    @Test
    public void contextLoads() {
        Assertions.assertTrue(1 == 1);
    }

}