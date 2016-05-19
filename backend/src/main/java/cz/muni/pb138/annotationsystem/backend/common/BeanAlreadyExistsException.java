package cz.muni.pb138.annotationsystem.backend.common;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class BeanAlreadyExistsException extends RuntimeException {

    public BeanAlreadyExistsException(String msg) {
        super(msg);
    }
}
