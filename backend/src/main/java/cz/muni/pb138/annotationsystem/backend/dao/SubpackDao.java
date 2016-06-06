package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.util.List;

/**
 * @author Josef Pavelec, Faculty of Informatics, Masaryk University
 */
public interface SubpackDao extends Dao<Subpack> {
    
    /**
     * Method to find all people which evaluate subpack
     * 
     * @param subpack input subpack which evaluate by people
     * @return people which evaluate subpack as List
     * @throws DaoException when some database error occurs
     */
    public List<Person> getPeopleAssignedToSubpack(Subpack subpack) throws DaoException;
    
    /**
     * Method to find all subpack which evaluate person
     * 
     * @param person input person who evaluates subpacks
     * @return subpacks which evaluate person as List
     * @throws DaoException when some database error occurs
     */
    public List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException;
    
    /**
     * Method erase assignment person to evaluate subpack
     * 
     * @param subpack subpack which evaluates person
     * @param person person who evaluates subpack
     * @throws DaoException when some database error occurs
     */
    public void deleteAssignmentPersonToSubpack(Subpack subpack, Person person) throws DaoException;
    
    /**
     * Method remove all assigned subpacks to person and update with input list of subpacks
     * 
     * @param person who evaluates answers in subpacks
     * @param assignedSubpacks subpacks which will be assigned to person to evaluate
     * @throws DaoException when some database error occurs
     */
    public void updateAssignment(Person person, List<Subpack> assignedSubpacks) throws DaoException;
    
    /**
     * Method remove all assigned people to subpack and update with input list of people
     * 
     * @param subpack which evaluates people
     * @param assignedPeople people which will be assigned to subpack to evaluate
     * @throws DaoException when some database error occurs
     */
    public void updateAssignment(Subpack subpack, List<Person> assignedPeople) throws DaoException;
    
    /**
     * Method add person to evaluate subpack
     * 
     * @param subpack subpack to evaluate
     * @param person person who will evaluate subpack
     * @throws DaoException when some database error occurs
     */
    public void assignPersonToSubpack(Subpack subpack, Person person) throws DaoException;

    /**
     * Method to find all subpack in pack
     * 
     * @param pack input pack
     * @return all subpacks as List
     * @throws DaoException when some database error occurs
     */
    public List<Subpack> getSubpacksInPack(Pack pack) throws DaoException;
    
    /**
     * Method to get time when subpack was assigned to person to evaluate
     * 
     * @param subpack input subpack
     * @param person input person
     * @return timestamp in Long datatype as time when was subpack assigned to person
     * @throws DaoException when some database error occurs
     */
    public Long getAssignationTime(Subpack subpack, Person person) throws DaoException;
    
    /**
     * Method to get time when subpack was completely evaluated by person
     * 
     * @param subpack input subpack
     * @param person input person
     * @return timestamp in Long datatype as time when was subpack completely evaluated by person
     * @throws DaoException when some database error occurs
     */
    public Long getCompletationTime(Subpack subpack, Person person) throws DaoException;
}
