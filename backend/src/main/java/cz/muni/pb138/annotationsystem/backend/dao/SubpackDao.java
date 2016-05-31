package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.util.List;

/**
 * @author Josef Pavelec, Faculty of Informatics, Masaryk University
 */
public interface SubpackDao extends Dao<Subpack> {
    
    public List<Person> getPersonsAssignedToSubpack(Subpack subpack) throws DaoException;

}
