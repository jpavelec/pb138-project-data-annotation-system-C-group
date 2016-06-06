package cz.muni.pb138.annotationsystem.backend.common;

/**
 * This exception is thrown when try insert same object into database double times
 * 
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class BeanAlreadyExistsException extends RuntimeException {

    /**
     * Constructs an instance of <code>BeanAlreadyExistsException</code> 
     * with the specified detail message.
     * @param msg the detail message.
     */
    public BeanAlreadyExistsException(String msg) {
        super(msg);
    }
}
