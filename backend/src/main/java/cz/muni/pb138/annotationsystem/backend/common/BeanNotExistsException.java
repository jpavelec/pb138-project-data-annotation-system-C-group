package cz.muni.pb138.annotationsystem.backend.common;

/**
 * This exception is thrown when entity which should exist doesn't exist
 * 
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class BeanNotExistsException extends RuntimeException {

    /**
     * Constructs an instance of <code>BeanNotExistsException</code> 
     * with the specified detail message.
     * @param msg the detail message.
     */
    public BeanNotExistsException(String msg) {
        super (msg);
    }
}
