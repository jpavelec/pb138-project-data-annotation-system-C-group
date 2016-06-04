package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
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
    private PackDao packDao;

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
        if (!packDao.doesExist(pack)) {
            throw new BeanNotExistsException("given pack does not exist");
        }

        return subpackDao.getSubpacksInPack(pack);
    }

    @Override
    @Transactional
    public List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("person is null");
        }
        if (person.getId() == null || person.getId() < 0) {
            throw new ValidationException("person id is null");
        }
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("given person does not exist");
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
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("given subpack does not exist");
        }

        return subpackDao.getPeopleAssignedToSubpack(subpack);
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
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("given person does not exist");
        }
        if (subpacks == null) {
            throw new IllegalArgumentException("list of subpacks is null");
        }
        for (Subpack s : subpacks) {
            if (s == null || s.getId() == null || s.getId() < 0) {
                throw new ValidationException("Some subpack is null or its id is null or negative");
            }
            if (!subpackDao.doesExist(s)) {
                throw new BeanNotExistsException("one of given subpack does not exist");
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
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("given subpack does not exist");
        }
        if (persons == null) {
            throw new IllegalArgumentException("persons is null");
        }
        for (Person p : persons) {
            if (p == null || p.getId() == null || p.getId() < 0) {
                throw new ValidationException("Some person is null or its id is null or negative");
            }
            if (!personDao.doesExist(p)) {
                throw new BeanNotExistsException("one of given person does not exist");
            }
        }

        subpackDao.updateAssignment(subpack, persons);

    }
}
