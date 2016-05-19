package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.model.Person;

/**
 * @author Josef Pavelec, Faculty of Informatics, Masaryk University
 */
public interface PersonDao extends Dao<Person> {
    
    public Person getByUsername(String username);
        
}
