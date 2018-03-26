package de.klabauter.test;

import de.klabauter.netlib.NetLib;

import java.util.Optional;

public class DummObjectNetLib extends NetLib<DummyObject> {

    public DummObjectNetLib() {
        super("http://localhost", "v1", Optional.of(8080));
    }

    @Override
    protected String serviceName() {
        return "dummyObject";
    }

    @Override
    protected Class responseClazz() {
        return DummyObject.class;
    }
}