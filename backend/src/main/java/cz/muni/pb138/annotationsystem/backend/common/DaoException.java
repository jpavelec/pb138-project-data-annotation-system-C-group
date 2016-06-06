package cz.muni.pb138.annotationsystem.backend.common;

/**
 * This exception is thrown when access to database fails
 * 
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class DaoException extends Exception {
    
    /**
     * Creates a new instance of <code>DaoException</code> 
     * without detail message.
     */
    public DaoException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>DaoException</code> 
     * with the specified detail message.
     * @param msg the detail message.
     */
    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
