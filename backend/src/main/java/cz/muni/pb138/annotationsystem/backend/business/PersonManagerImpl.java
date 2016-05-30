package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.common.BeanAlreadyExistsException;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.PersonDao;
import cz.muni.pb138.annotationsystem.backend.dao.PersonDaoImpl;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import org.springframework.dao.PessimisticLockingFailureException;

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
    public void createPerson(Person person) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("Created person is null");
        }
        if (person.getId() != null) {
            throw new ValidationException("Created person already has id: "+person);
        }
        if (person.getUsername() == null || person.getUsername().isEmpty()) {
            throw new ValidationException("Created person has to have username: "+person);
        }
        if (personDao.getByUsername(person.getUsername()) != null) {
            throw new BeanAlreadyExistsException("Person with name: "+person.getUsername()+" already exists.");
        }
        personDao.create(person);
    }

    @Override
    public Person getPersonByUsername(String username) throws DaoException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is null or empty");
        }
        return personDao.getByUsername(username);
    }

    @Override
    public Person getPersonById(Long id) throws DaoException {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Id is null or negative");
        }
        Person p = personDao.getById(id);
        return p;
    }

    @Override
    public List<Person> getAllPersons() throws DaoException {
        List<Person> all = personDao.getAll();
        return all;
    }
}
