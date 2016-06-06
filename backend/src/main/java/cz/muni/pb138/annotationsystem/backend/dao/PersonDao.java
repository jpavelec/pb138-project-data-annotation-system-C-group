package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Person;

/**
 * @author Josef Pavelec, Faculty of Informatics, Masaryk University
 */
public interface PersonDao extends Dao<Person> {
    
    /**
     * Find person by specific username. 
     * @param username String username to find
     * @return person with input username or null when doesn't exist
     * @throws DaoException when some database error occurs
     */
    public Person getByUsername(String username) throws DaoException;
        
}
