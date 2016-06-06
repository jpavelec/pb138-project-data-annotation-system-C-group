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

    /**
     * @param id of requested subpack
     * @return Subpack with given id
     * @throws DaoException
     */
    Subpack getSubpackById(Long id) throws DaoException;

    /**
     * @param pack pack form which we requested subpacks
     * @return list of sibpacks in given pack
     * @throws DaoException
     */
    List<Subpack> getSubpacksInPack(Pack pack) throws DaoException;

    /**
     * @param person person which is assigned to subpacks
     * @return list of subpacks which person is assigned to evaluate
     * @throws DaoException
     */
    List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException;

    /**
     * @param subpack subpack to which are persons assigned
     * @return list of persons which are assigned to evaluate the subpack
     * @throws DaoException
     */
    List<Person> getPersonsAssignedToSubpack(Subpack subpack) throws DaoException;

    /**
     * Update assigment of person. All given subpacks has to be from given pack.
     * Exception is thrown otherwise.
     * Manipulate only with assigments in given pack.
     *
     * @param person person to be updated
     * @param subpacks to be assigned to person
     * @throws DaoException
     */
    void updatePersonsAssignment(Person person, Pack pack, List<Subpack> subpacks) throws DaoException;


}
