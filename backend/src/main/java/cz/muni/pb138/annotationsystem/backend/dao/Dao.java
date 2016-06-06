package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface Dao<B extends Object> {

    /**
     * Insert a bean object into database. Method assigns an unique id into bean object.
     * 
     * @param bean object to create
     * @throws DaoException when some database error occurs
     */
    void create(B bean) throws DaoException;

    /**
     * Find existing bean object by his unique id value.
     * 
     * @param id to find
     * @return bean object with input id
     * @throws DaoException when some database error occurs
     */
    B getById(Long id) throws DaoException;

    /**
     * Method for find all bean objects.
     * 
     * @return bean objects as List
     * @throws DaoException when some database error occurs
     */
    List<B> getAll() throws DaoException;

    /**
     * Method for update bean object.
     * 
     * @param bean bean object to update
     * @throws DaoException when some database error occurs
     */
    void update(B bean) throws DaoException;

    /**
     * Method for delete bean object.
     * 
     * @param bean bean object to delete
     * @throws DaoException when some database error occurs
     */
    void delete(B bean) throws DaoException;
    
    /**
     * Method check if bean object exists in database.
     * 
     * @param bean bean object to check if exists in database
     * @return true when database contains input bean object. False otherwise
     * @throws DaoException when some database error occurs
     */
    boolean doesExist(B bean) throws DaoException;

}
