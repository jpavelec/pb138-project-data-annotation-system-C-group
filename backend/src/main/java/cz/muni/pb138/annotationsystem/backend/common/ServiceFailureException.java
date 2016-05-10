package cz.muni.pb138.annotationsystem.backend.common;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
public class ServiceFailureException extends RuntimeException {

    public ServiceFailureException(String msg) {
        super(msg);
    }

    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
