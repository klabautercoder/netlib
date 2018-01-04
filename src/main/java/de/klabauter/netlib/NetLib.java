package de.klabauter.netlib;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j

public abstract class NetLib<RESPONSE extends Object> {
    private String apiGateway;
    private String apiVersion;
    private int internalPort = 8080;

    protected String realApiUrl;

    private Gson gson = new Gson();

    public NetLib(
            String apiGateway,
            String apiVersion
    ) {
        this.apiGateway = apiGateway;
        this.apiVersion = apiVersion;
        setUrlToMicroservice();
    }

    private void setUrlToMicroservice() {
        if (isSwarmServiceUrlAccessable()) {
            realApiUrl = "http://" + serviceName() + "-" + apiVersion + ":" + internalPort + "/";
        } else {
            realApiUrl = apiGateway + serviceName() + "/";
        }

        log.info("Accessing {} via {}", serviceName(), realApiUrl);
    }

    /**
     * Prüft ob die Microservice Swarm Urls greifen.
     * Wenn nicht wird auf die ApiGW Url zurückgegriffen.
     * @return
     */
    private boolean isSwarmServiceUrlAccessable() {
        log.info("Checking Microservice : {}-{}:{}", serviceName(), apiVersion, internalPort);
        try (Socket s = new Socket(serviceName() + "-" + apiVersion, internalPort)) {
            return true;
        } catch (IOException ex) {
            /* ignore */
        }
        return false;
    }

    protected abstract String serviceName();

    protected abstract Class responseClazz();

    public RESPONSE getData(String url) throws Exception {
        url = realApiUrl + url;
        log.debug("Requesting {}", url);
        HttpResponse<String> data = Unirest.get(url).asString();

        if (data.getStatus() != 200) {
            NetLibException exception = new NetLibException();
            exception.setUrl(url);
            exception.setErrorCode(data.getStatus());

            setUrlToMicroservice(); // Erneut Verbindung prüfen
            throw exception;
        }

        if (data.getBody() == null || data.getBody().isEmpty()) {
            throw new Exception("Anfrage " + url + " gab einen leeren Response.Status vom Microservice : " + data.getStatus());
        }
        return (RESPONSE) Unirest.get(url).asObject(responseClazz()).getBody();

    }

    public List<RESPONSE> getDataAsList(String url) throws UnirestException, NetLibException {
        url = realApiUrl + url;

        HttpResponse<String> str = Unirest.get(url).asString();
        try {
            Object[] array = (Object[]) java.lang.reflect.Array.newInstance(responseClazz(), 1);
            RESPONSE[] mcArray = gson.fromJson(str.getBody(), (Type) array.getClass());
            List<RESPONSE> ret = new LinkedList<>();
            ret.addAll(Arrays.asList(mcArray));
            return ret;
        } catch (Exception exp) {
            NetLibException exp2 = new NetLibException(exp.getMessage());
            exp2.setUrl(url);
            exp2.setErrorCode(str.getStatus());

            setUrlToMicroservice(); // Erneut Verbindung prüfen
            throw exp2;
        }

    }

    public List<Integer> getIdsFrom(String url) throws UnirestException, NetLibException {
        url = realApiUrl + url;

        HttpResponse<String> str = Unirest.get(url).asString();
        try {
            Object[] array = (Object[]) java.lang.reflect.Array.newInstance(Integer.class, 1);
            Integer[] mcArray = gson.fromJson(str.getBody(), (Type) array.getClass());
            List<Integer> ret = new LinkedList<>();
            ret.addAll(Arrays.asList(mcArray));
            return ret;
        } catch (Exception exp) {
            NetLibException exp2 = new NetLibException(exp.getMessage());
            exp2.setUrl(url);
            exp2.setErrorCode(str.getStatus());

            setUrlToMicroservice(); // Erneut Verbindung prüfen
            throw exp2;
        }
    }
}
