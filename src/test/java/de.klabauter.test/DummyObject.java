package de.klabauter.test;

import lombok.Data;

import java.util.Random;
import java.util.UUID;

@Data
public class DummyObject {


    public DummyObject() {
        Random random = new Random();
        id = random.nextInt();
        name = UUID.randomUUID().toString();
    }

    public DummyObject(int i) {
        id = i;
        name = UUID.randomUUID().toString();
    }

    private int id;

    private String name;
}
