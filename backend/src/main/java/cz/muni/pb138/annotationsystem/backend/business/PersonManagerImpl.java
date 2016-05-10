package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.PersonDao;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class PersonManagerImpl implements PersonManager {

    @Inject
    private PersonDao personDao;

    @Override
    public void createPerson(Person person) {
    }

    @Override
    public Person getPersonByUsername(String username) {
        Person p = new Person("John Snow", true);
        p.setId((long) 1);
        return p;
    }

    @Override
    public Person getPersonById(Long id) {
        Person p = new Person("Tyrion Lannister", false);
        p.setId((long) 2);
        return p;
    }

    @Override
    public List<Person> getAllPersons() {
        Person p1 = new Person("Tyrion Lannister", false);
        p1.setId((long) 2);
        Person p2 = new Person("John Snow", true);
        p2.setId((long) 1);
        List<Person> persons = new ArrayList<>();
        persons.add(p1);
        persons.add(p2);
        return persons;
    }
}
