package pl.woelke.gus_api.rest.exception;

public class SOAPProtocolException extends Exception {

    public SOAPProtocolException(String message) {
        super(message);
    }

    public SOAPProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public SOAPProtocolException(Throwable cause) {
        super(cause);
    }
}
