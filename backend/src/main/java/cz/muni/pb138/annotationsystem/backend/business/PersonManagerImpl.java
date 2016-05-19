package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
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
        personDao.create(person);
    }

    @Override
    public Person getPersonByUsername(String username) throws DaoException {
        /*List<Person> all = personDao.getAll();
        Person result = null;
        for (Person p : all) {
            if (p.getUsername().equals(username)) {
                result = p;
                break;
            }
        }
        if (result == null) {
            throw new BeanNotExistsException();
        }
        return result;*/
        return personDao.getByUsername(username);
    }

    @Override
    public Person getPersonById(Long id) throws DaoException {
        Person p = personDao.getById(id);
        return p;
    }

    @Override
    public List<Person> getAllPersons() throws DaoException {
        List<Person> all = personDao.getAll();
        return all;
    }
}
