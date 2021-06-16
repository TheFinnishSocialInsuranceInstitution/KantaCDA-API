package fi.kela.kanta.exceptions;

public class PurkuException extends Exception {

    private static final long serialVersionUID = 412351L;

    public PurkuException() {
        super();
    }

    public PurkuException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PurkuException(String message, Throwable cause) {
        super(message, cause);
    }

    public PurkuException(String message) {
        super(message);
    }

    public PurkuException(Throwable cause) {
        super(cause);
    }
}
