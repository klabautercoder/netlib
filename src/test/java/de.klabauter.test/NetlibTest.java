package de.klabauter.test;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.klabauter.netlib.NetLibException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

/**
 * General Tests.
 * <p>
 * First Mock some Services for testing. After that use the test Netlib Implementations
 * to talk to this services. Profit...
 *
 * @see <a href="https://specto.io/blog/2017/1/4/stubbing-http-apis-and-microservices-with-the-hoverfly-java-dsl/">Hoverfly</a>
 */
@ExtendWith(HoverflyExtension.class)
class NetlibTest {

    private Gson gson = new Gson();

    private DummyObjectNetLib lib = new DummyObjectNetLib();

    @BeforeAll
    public static void init() {
        Unirest.setObjectMapper(new UniRestDataObjectMapper());
    }

    @Test
    public void contextLoads() throws NetLibException, UnirestException {
        List<Integer> id = lib.getIdsFrom(""); // Get Everything
        Assertions.assertTrue(id != null);
    }

}