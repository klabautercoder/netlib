package de.klabauter.netlib;

import lombok.Getter;
import lombok.Setter;

public class NetLibException extends Exception {

    @Setter
    @Getter
    private String url;

    @Setter
    @Getter
    private int errorCode = -1;

    public NetLibException() {}

    public NetLibException(String exp) {
        super(exp);
    }

    public  String toString() {
        StringBuilder error = new StringBuilder();

        error.append("Error Calling: ").append(url).append(". <br/>\n");

        if (errorCode > -1) {
            error.append("Response Code: ").append(errorCode).append(" <br/>\n");
        }

        error.append("Error Message:").append(this.getMessage());

        return error.toString();
    }
}
