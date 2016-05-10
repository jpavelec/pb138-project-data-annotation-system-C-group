package cz.muni.pb138.annotationsystem.backend.common;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class DaoException extends Exception {
    
    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
