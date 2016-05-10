package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface PersonManager {

    void createPerson(Person person);

    Person getPersonByUsername(String username);

    Person getPersonById(Long id);

    List<Person> getAllPersons();

}
