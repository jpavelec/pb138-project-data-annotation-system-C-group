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
    
    public List<Person> getPeopleAssignedToSubpack(Subpack subpack) throws DaoException;
    
    public List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException;
    
    public void deleteAssignmentPersonToSubpack(Subpack subpack, Person person) throws DaoException;
    
    public void updateAssignment(Person person, List<Subpack> assignedSubpacks) throws DaoException;
    
    public void updateAssignment(Subpack subpack, List<Person> assignedPeople) throws DaoException;
    
    public void assignPersonToSubpack(Subpack subpack, Person person) throws DaoException;

    public List<Subpack> getSubpacksInPack(Pack pack) throws DaoException;
    
    public Long getAssignationTime(Subpack subpack, Person person) throws DaoException;
    
    public void setCompletationTime(Subpack subpack, Person person) throws DaoException;
    
    public Long getCompletationTime(Subpack subpack, Person person) throws DaoException;
}
