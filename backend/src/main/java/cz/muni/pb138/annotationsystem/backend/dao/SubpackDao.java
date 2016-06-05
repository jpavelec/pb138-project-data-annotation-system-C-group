package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Josef Pavelec, Faculty of Informatics, Masaryk University
 */
public interface SubpackDao extends Dao<Subpack> {
    
    public List<Person> getPeopleAssignedToSubpack(Subpack subpack) throws DaoException;
    
    public List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException;
    
    public void updateAssignment(Person person, List<Subpack> assignedSubpacks) throws DaoException;
    
    public void updateAssignment(Subpack subpack, List<Person> assignedPeople) throws DaoException;
    
    public void assignPersonToSubpack(Person person, Subpack subpack) throws DaoException;

    public List<Subpack> getSubpacksInPack(Pack pack) throws DaoException;
    
    public Timestamp getAssignationTime(Subpack subpack, Person person) throws DaoException;
}
