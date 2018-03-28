package de.klabauter.test;

import com.google.gson.Gson;
import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import io.specto.hoverfly.junit5.api.HoverflySimulate;
import org.junit.jupiter.api.extension.ExtensionContext;

import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.created;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;

// @TODO: check if hoverfly is only spring compatible
public class HoverflyExtension
        extends io.specto.hoverfly.junit5.HoverflyExtension {

    /*
    private HoverflyRule rule = HoverflyRule.inSimulationMode(
            SimulationSource.dsl(
                    service("localhost")
                            .get("/1")
                            .willReturn(success(json(new DummyObject(1))))
                            .post("/")
                            .body(json(new Gson().toJson(new DummyObject())))
                            .willReturn(created("http://www.booking-service.com/api/bookings/1"))
            )
    );
    */
}

