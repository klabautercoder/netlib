package de.klabauter.test;

import de.klabauter.netlib.NetLib;

/**
 * This netlib is used in unit tests.
 */
public class DummyObjectNetLib extends NetLib<DummyObject> {

    public DummyObjectNetLib() {
        super("http://localhost/", "v1", java.util.Optional.of(80));
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
