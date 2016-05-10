package cz.muni.pb138.annotationsystem.backend.common;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
public class IllegalEntityException extends RuntimeException {

    public IllegalEntityException() {
    }

    public IllegalEntityException(String msg) {
        super(msg);
    }

    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }    
}
