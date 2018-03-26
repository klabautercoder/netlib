package de.klabauter.netlib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Basisklasse für die REST Kommunikation mit der Microservice
 * Landschaft. Aktuell läuft die Kommunikation nur innerhalb eines Stacks.
 *
 * @param <R> - Objekttyp, mit dem die Schnittstelle in ihrer Implementierung arbeitet
 */

    @Slf4j
    public abstract class NetLib<R> {

        private String apiGateway;  // Config
        private String apiVersion;  // Config

        private int internalPort = 8080;

        protected String realApiUrl;

        // Wie oft haben wir gegen den Microservice getestet
        private int retryCount = 0;

        private Gson gson;

        public NetLib(
                String apiGateway,
                String apiVersion,
                Optional<Integer> port
        ) {

            this.apiGateway = apiGateway;
            this.apiVersion = apiVersion;

            if (port.isPresent())
                internalPort = port.get();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            gson = new GsonBuilder().registerTypeAdapter(
                    LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                            (json, type, jsonDeserializationContext) -> {
                                Instant instant = null;
                                try {
                                    String date = json.getAsJsonPrimitive().getAsString();
                                    instant = sdf.parse(json.getAsJsonPrimitive().getAsString()).toInstant();
                                    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Berlin"));
                                    return zonedDateTime.toLocalDateTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return null;
                                }

                            }).create();

            setUrlToMicroservice();
        }

        /**
         * Die URL entscheiden.
         */
        private void setUrlToMicroservice() {
            if (isSwarmServiceUrlAccessable()) {
                realApiUrl = "http://" + serviceName() + "-" + apiVersion + ":" + internalPort + "/";
            } else {
                realApiUrl = apiGateway + serviceName() + "/";
            }
            retryCount = 0; // Neu geschaut resettet die Retrys im Allgemeinen.
        }

        /**
         * Prüft ob die Microservice Swarm Urls greifen.
         * Wenn nicht wird auf die ApiGW Url zurückgegriffen.
         *
         * @return
         */
        private boolean isSwarmServiceUrlAccessable() {
            try (Socket ignored = new Socket(serviceName() + "-" + apiVersion, internalPort)) {
                return true;
            } catch (IOException ex) {
                /* ignore */
            }
            return false;
        }

        protected abstract String serviceName();

        protected abstract Class responseClazz();

        /**
         * Einfaches holen eines einzigen Datenobjektes
         *
         * @param url - Teilurl zum Element
         * @return Datenobjekt oder null
         * @throws UnirestException - Microservice falsch
         * @throws NetLibException  - Server falsch/weg
         */
        public R getData(String url)
                throws UnirestException, NetLibException {
            url = realApiUrl + url;

            HttpResponse<String> data = Unirest.get(url).asString();

            // @TODO: Die Logik nochmal nachprüfen
            if ((data.getBody() == null || data.getBody().isEmpty())
                    || (data.getStatus() != 200)) {
                NetLibException exception = new NetLibException();
                exception.setUrl(url);
                exception.setErrorCode(data.getStatus());
                setUrlToMicroservice(); // Erneut Verbindung prüfen
                throw exception;
            }

            try {

                String contents = data.getBody();
                R rdata = (R) gson.fromJson(contents, responseClazz());
                return rdata;
            } catch (JsonSyntaxException exp) {
                // Microservice hatte Probleme. Wir versuchen es nochmal?
                retryCount++;
                if (retryCount < 4)
                    getDataAsList(url); // Nochmal versuchen
            } catch (Exception exp) {
                log.error(exp.getMessage(), exp);
                setUrlToMicroservice();
            }

            retryCount = 0; // Resetten damit der nächste Call nicht Probleme bekommt;
            return null;
        }

        /**
         * @param url - URL Aufruf ohne http://<SERVER />
         * @return
         * @throws UnirestException - Request Probleme
         * @throws NetLibException  - Alle andere Probleme
         */
        public List<R> getDataAsList(String url)
                throws UnirestException, NetLibException {

            url = realApiUrl + url;

            HttpResponse<String> str = Unirest.get(url).asString();
            try {
                Object[] array = (Object[]) java.lang.reflect.Array.newInstance(responseClazz(), 1);
                R[] mcArray = gson.fromJson(str.getBody(), (Type) array.getClass());
                List<R> ret = new LinkedList<>();
                ret.addAll(Arrays.asList(mcArray));
                return ret;
            } catch (JsonSyntaxException exp) {
                // Microservice hatte Probleme. Wir versuchen es nochmal?
                retryCount++;
                if (retryCount < 4)
                    getDataAsList(url); // Nochmal versuchen
            } catch (Exception exp) {
                // Microservice ist nicht da?
                NetLibException exp2 = new NetLibException(exp.getMessage());
                exp2.setUrl(url);
                exp2.setErrorCode(str.getStatus());
                setUrlToMicroservice(); // Erneut Verbindung prüfen
                throw exp2;
            }

            retryCount = 0; // Resetten damit der nächste Call nicht Probleme bekommt;
            return new ArrayList<>();
        }

        /**
         * @param url - URL Aufruf ohne http://<SERVER />
         * @return Liste von Ids
         * @throws UnirestException - Request Probleme
         * @throws NetLibException  - Alle andere Probleme
         */
        public List<Integer> getIdsFrom(String url)
                throws UnirestException, NetLibException {

            url = realApiUrl + url;

            HttpResponse<String> str = Unirest.get(url).asString();
            try {
                Object[] array = (Object[]) java.lang.reflect.Array.newInstance(Integer.class, 1);
                Integer[] mcArray = gson.fromJson(str.getBody(), (Type) array.getClass());
                List<Integer> ret = new LinkedList<>();
                ret.addAll(Arrays.asList(mcArray));
                return ret;
            } catch (JsonSyntaxException exp) {
                // Microservice hatte Probleme. Wir versuchen es nochmal?
                retryCount++;
                if (retryCount < 4)
                    getDataAsList(url); // Nochmal versuchen
            } catch (Exception exp) {
                NetLibException exp2 = new NetLibException(exp.getMessage());
                exp2.setUrl(url);
                exp2.setErrorCode(str.getStatus());
                setUrlToMicroservice(); // Erneut Verbindung prüfen
                throw exp2;
            }
            retryCount = 0; // Resetten damit der nächste Call nicht Probleme bekommt;
            return new ArrayList<>();
        }
    }
