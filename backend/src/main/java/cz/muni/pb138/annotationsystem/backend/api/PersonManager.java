package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface PersonManager {

    Person getOrCreatePersonByUsername(String username) throws DaoException;

    Person getPersonById(Long id) throws DaoException;

    List<Person> getAllPersons() throws DaoException;

}
