package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.SubpackDao;
import cz.muni.pb138.annotationsystem.backend.dao.SubpackDaoImpl;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

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
    private PackManager packManager;

    @Inject
    private AnswerManager answerManager;

    @Inject
    private PersonManager personManager;

    @Override
    public Subpack getSubpackById(Long id) throws DaoException {
        Subpack s = new Subpack(packManager.getPackById((long) 1), "animals 2");
        s.setId((long) 3);
        s.setUsers(personManager.getAllPersons());
        return s;
    }

    @Override
    public List<Subpack> getSubpacksInPack(Pack pack) throws DaoException {
        List<Subpack> subpacks = new ArrayList<>();

        Subpack s1 = new Subpack(packManager.getPackById((long) 1), "animals 2");
        s1.setId((long) 3);
        s1.setUsers(personManager.getAllPersons());

        Subpack s2 = new Subpack(packManager.getPackById((long) 1), "animals 2");
        s2.setId((long) 2);
        s2.setUsers(personManager.getAllPersons());

        subpacks.add(s1);
        subpacks.add(s2);
        return subpacks;

    }

    @Override
    public List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException {
        List<Subpack> subpacks = new ArrayList<>();

        Subpack s1 = new Subpack(packManager.getPackById((long) 1), "animals 2");
        s1.setId((long) 3);
        s1.setUsers(personManager.getAllPersons());

        Subpack s2 = new Subpack(packManager.getPackById((long) 1), "animals 2");
        s2.setId((long) 2);
        s2.setUsers(personManager.getAllPersons());

        subpacks.add(s1);
        subpacks.add(s2);
        return subpacks;
    }

    @Override
    public List<Person> getPersonsAssignedToSubpack(Subpack subpack) throws DaoException {
        return personManager.getAllPersons();
    }

    @Override
    public void updatePersonsAssignment(Person person, List<Subpack> subpacks) {

    }

    @Override
    public void updateSubpacksAssignment(Subpack subpack, List<Person> persons) {

    }
}
