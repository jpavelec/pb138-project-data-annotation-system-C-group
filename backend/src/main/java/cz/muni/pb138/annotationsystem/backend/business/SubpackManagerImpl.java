package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.PackDao;
import cz.muni.pb138.annotationsystem.backend.dao.PersonDao;
import cz.muni.pb138.annotationsystem.backend.dao.SubpackDao;
import cz.muni.pb138.annotationsystem.backend.dao.SubpackDaoImpl;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class SubpackManagerImpl implements SubpackManager {

    @Inject
    private SubpackDao subpackDao;

    @Inject
    private PersonDao personDao;

    @Override
    @Transactional
    public Subpack getSubpackById(Long id) throws DaoException {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id is null or negative");
        }

        return subpackDao.getById(id);
    }

    @Override
    @Transactional
    public List<Subpack> getSubpacksInPack(Pack pack) throws DaoException {
        if (pack == null) {
            throw new IllegalArgumentException("pack is null");
        }
        if (pack.getId() == null || pack.getId() < 0) {
            throw new IllegalArgumentException("pack id is null");
        }

        List<Subpack> subpacks = new ArrayList<>();
        for (Subpack s : subpackDao.getAll()) {
            if (s.getParent().equals(pack)) {
                subpacks.add(s);
            }
        }

        return subpacks;
    }

    @Override
    @Transactional
    public List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("person is null");
        }
        if (person.getId() == null || person.getId() < 0) {
            throw new IllegalArgumentException("person id is null");
        }

        return subpackDao.getSubpacksAssignedToPerson(person);
    }

    @Override
    @Transactional
    public List<Person> getPersonsAssignedToSubpack(Subpack subpack) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new IllegalArgumentException("subpack id is null");
        }

        List<Person> persons = new ArrayList<>();
        for (Person p : personDao.getAll()) {
            if (getSubpacksAssignedToPerson(p).contains(subpack)) {
                persons.add(p);
            }
        }

        return persons;
    }

    @Override
    @Transactional
    public void updatePersonsAssignment(Person person, List<Subpack> subpacks) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("person is null");
        }
        if (person.getId() == null || person.getId() < 0) {
            throw new IllegalArgumentException("person id is null");
        }
        if (subpacks == null) {
            throw new IllegalArgumentException("list of subpacks is null");
        }
        for (Subpack s : subpacks) {
            if (s == null || s.getId() == null || s.getId() < 0) {
                throw new ValidationException("Some subpack is null or its id is null or negative");
            }
        }

        subpackDao.updateAssignment(person, subpacks);
    }

    @Override
    @Transactional
    public void updateSubpacksAssignment(Subpack subpack, List<Person> persons) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new IllegalArgumentException("subpack id is null");
        }
        if (persons == null) {
            throw new IllegalArgumentException("persons is null");
        }
        for (Person p : persons) {
            if (p == null || p.getId() == null || p.getId() < 0) {
                throw new ValidationException("Some person is null or its id is null or negative");
            }
        }


    }
}
