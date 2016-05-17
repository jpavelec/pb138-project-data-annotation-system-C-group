package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface SubpackManager {

    Subpack getSubpackById(Long id) throws DaoException;

    List<Subpack> getSubpacksInPack(Pack pack) throws DaoException;

    List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException;

    List<Person> getPersonsAssignedToSubpack(Subpack subpack) throws DaoException;


    void updatePersonsAssignment(Person person, List<Subpack> subpacks);

    void updateSubpacksAssignment(Subpack subpack, List<Person> persons);

}
