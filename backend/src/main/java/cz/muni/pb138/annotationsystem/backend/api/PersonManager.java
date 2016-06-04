package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface PersonManager {

    /**
     * Return person with the given username if exists. Create new one otherwise. Username is unique.
     *
     * @param username Uniwue username of person
     * @return person with given username or created person with setted id
     * @throws DaoException
     */
    Person getOrCreatePersonByUsername(String username) throws DaoException;

    /**
     * @param id Id of person
     * @return Person with given id
     * @throws DaoException
     */
    Person getPersonById(Long id) throws DaoException;

    /**
     * @return List of all persons in the system
     * @throws DaoException
     */
    List<Person> getAllPersons() throws DaoException;

}
