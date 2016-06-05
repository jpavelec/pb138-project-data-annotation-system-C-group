package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ServiceFailureException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@Named
public class SubpackDaoImpl implements SubpackDao {
    
    @Inject
    private DataSource dataSource;
    
    @Inject
    private PersonDaoImpl personDao;
    
    @Inject
    private AnswerDaoImpl answerDao;
    
    @Inject
    private PackDaoImpl packDao;

    public SubpackDaoImpl() {
    }
    
    

    public SubpackDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void validate(Subpack subpack) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getParent() == null) {
            throw new ValidationException("Subpack parent pack is null");
        }
        if (subpack.getParent().getId() == null) {
            throw new ValidationException("Subpack parent pack has null id");
        }
        checkIfPackExists(subpack.getParent());
        if (subpack.getName() == null) {
            throw new ValidationException("Subpack name is null");
        }
        if (subpack.getName().isEmpty()) {
            throw new ValidationException("Subpack name is empty");
        }
        if (subpack.getName().length() > 60) {
            throw new ValidationException("Subpack name is too long (" +
                    subpack.getName().length() + " chars but 60 is maximum");
        }
    }
    
    private void checkIfPackExists(Pack pack) throws DaoException {
        try {
            packDao.getById(pack.getId());
        } catch(BeanNotExistsException ex) {
            String msg = "Error when creating/updating subpack - parent pack with id "
                    + pack.getId() + " is not exist";
            throw new BeanNotExistsException(msg);
        }
    }
    

    private Long getKey(ResultSet keyRS, Subpack subpack) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert subpack " + subpack
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert subpack " + subpack
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert subpack " + subpack
                    + " - no key found");
        }
    }
    
    @Override
    public boolean doesExist(Subpack subpack) throws DaoException {
        checkDataSource();
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getId() == null) {
            throw new ValidationException("Subpack id is null");
        }
        if (subpack.getId() < 0) {
            throw new ValidationException("Pack id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id FROM subpack WHERE id = ?")) {
            
            st.setLong(1, subpack.getId());
            
            try (ResultSet rs = st.executeQuery()) {
            
                if (rs.next()) {
                    if (rs.next()) {
                        throw new DaoException(
                            "Internal error: More entities with the same id found!");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(
                "Error when testing if subpack " + subpack + " is in DB", ex);
        }
    }
    
    @Override
    public void create(Subpack subpack) throws DaoException {
        checkDataSource();
        validate(subpack);
        if (subpack.getId() != null) {
            throw new ValidationException("Subpack id is already set");
        }

        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO subpack (packid, name) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
            
            st.setLong(1, subpack.getParent().getId());
            st.setString(2, subpack.getName());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert subpack " + subpack);
            }
            
            try (ResultSet keyRS = st.getGeneratedKeys()) {
                subpack.setId(getKey(keyRS, subpack));
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting subpack " + subpack, ex);
        }
    }

    private Subpack resultSetToSubpack(ResultSet rs) throws SQLException, DaoException {
        Subpack subpack = new Subpack();
        subpack.setId(rs.getLong("id"));
        Pack pack = new Pack();
        PackDaoImpl packDao = new PackDaoImpl(dataSource);
        pack = packDao.getById(rs.getLong("packid"));
        subpack.setParent(pack);
        subpack.setName(rs.getString("name"));
        //subpack.setUsers(getUsersAssignedToSubpackBySubpackId(subpack.getId()));
        return subpack;
    }
    @Override
    public Subpack getById(Long id) throws DaoException {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("Id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, packid, name FROM subpack WHERE id = ?")) {
            
            st.setLong(1, id);
            
            ResultSet rs = st.executeQuery();
            
                if (rs.next()) {
                    Subpack subpack = resultSetToSubpack(rs);

                    if (rs.next()) {
                        throw new ServiceFailureException(
                            "Internal error: More entities with the same id found " +
                            "(source id: " + id + ", found " + subpack + 
                            " and " + resultSetToSubpack(rs));
                    }
                    return subpack;
                } else {
                    String msg = "Subpack with id "+id+" was not found.";
                    throw new BeanNotExistsException(msg);
                }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when retriving subpack with id " + id, ex);
        }
    }

    @Override
    public List<Subpack> getAll() throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, packid, name FROM subpack");
            ResultSet rs = st.executeQuery()) {
            List<Subpack> subpacks = new ArrayList<>();
            while (rs.next()) {
                subpacks.add(resultSetToSubpack(rs));
            }
            
            return subpacks;
            
        } catch (SQLException ex) {
            String msg = "Error when getting all subpacks from DB";
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void update(Subpack subpack) throws DaoException {
        checkDataSource();
        validate(subpack);
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("Subpack id is null or negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "UPDATE subpack SET packid = ?, name = ? WHERE id = ?")) {
            
            st.setLong(1, subpack.getParent().getId());
            st.setString(2, subpack.getName());
            st.setLong(3, subpack.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("Subpack" + subpack + "wasn't found in database");
            } else if (count != 1) {
                throw new ServiceFailureException(
                "Invalid updated rows count detected (one row should be updated): " + count);
            }
            
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating subpack " + subpack, ex);
        }
    }

    @Override
    public void delete(Subpack subpack) throws DaoException {
        checkDataSource();
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getId() == null) {
            throw new ValidationException("Subpack id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "DELETE FROM subpack WHERE id = ?")) {
            
            st.setLong(1, subpack.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("Subpack " + subpack + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException(
                        "Invalid deleted rows count detected (one row should be updated): " + count);
            }
            
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when deleting subpack " + subpack, ex);
        }
        
    }

    @Override
    public List<Person> getPeopleAssignedToSubpack(Subpack subpack) throws DaoException {
        checkDataSource();
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getId() == null) {
            throw new ValidationException("Subpack id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT personid FROM assignedperson WHERE subpackid = ?")) {
            
            st.setLong(1, subpack.getId());
            
            ResultSet rs = st.executeQuery();
            List<Person> assignedPeople = new ArrayList<>();
            
            while (rs.next()) {
                assignedPeople.add(personDao.getById(rs.getLong(1)));
            }
            
            return assignedPeople;
            
        } catch (SQLException ex) {
            String msg = "Error when retriving assigned people to subpack with id "+subpack.getId();
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Subpack> getSubpacksAssignedToPerson(Person person) throws DaoException {
        checkDataSource();
        if (person == null) {
            throw new IllegalArgumentException("Person is null");
        }
        if (person.getId() == null) {
            throw new ValidationException("Person id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT subpackid FROM assignedperson WHERE personid = ?")) {
            
            st.setLong(1, person.getId());
            
            ResultSet rs = st.executeQuery();
            List<Subpack> assignedSubpacks = new ArrayList<>();
            
            while (rs.next()) {
                assignedSubpacks.add(this.getById(rs.getLong(1)));
            }
            
            return assignedSubpacks;
            
        } catch (SQLException ex) {
            String msg = "Error when retriving assigned people to subpack with id "+person.getId();
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void updateAssignment(Person person, List<Subpack> assignedSubpacks) throws DaoException {
        try (Connection connection = dataSource.getConnection()) {
            deleteAssignmentToPerson(person);
            for (Subpack subpack : assignedSubpacks) {
                try (PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO assignedperson (subpackid, personid, starttime) VALUES (?,?,?)")) {
                    st.setLong(1, subpack.getId());
                    st.setLong(2, person.getId());
                    Calendar calendar = Calendar.getInstance();
                    st.setTimestamp(3, new java.sql.Timestamp(calendar.getTime().getTime()));
                    int addedRows = st.executeUpdate();
                    if (addedRows != 1) {
                        throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert assignedperson " + subpack);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating assignedperson " + person, ex);
        }
    }

    @Override
    public void updateAssignment(Subpack subpack, List<Person> assignedPeople) throws DaoException {
        try (Connection connection = dataSource.getConnection()) {
            deleteAssignmentToSubpack(subpack);
            for (Person person : assignedPeople) {
                try (PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO assignedperson (subpackid, personid, starttime) VALUES (?,?,?)")) {
                    st.setLong(1, subpack.getId());
                    st.setLong(2, person.getId());
                    Calendar calendar = Calendar.getInstance();
                    st.setTimestamp(3, new java.sql.Timestamp(calendar.getTime().getTime()));
                    int addedRows = st.executeUpdate();
                    if (addedRows != 1) {
                        throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert assignedperson " + person);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating assignedperson " + subpack, ex);
        }
    }
    
    private void deleteAssignmentToSubpack(Subpack subpack) throws DaoException {
        try (Connection connection = dataSource.getConnection(); 
            PreparedStatement st = connection.prepareStatement(
                    "DELETE FROM assignedperson WHERE subpackid = ?")) {
                    st.setLong(1, subpack.getId());
                    int assignedPeopleCount = this.getPeopleAssignedToSubpack(subpack).size();
                    int deletedRows = st.executeUpdate();
                    if (assignedPeopleCount != deletedRows) {
                        throw new ServiceFailureException("Error when deleting assignment people "
                            + "to subpack. "+assignedPeopleCount+" should delete but was " + deletedRows);
                    }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when deleting assignment people to subpack " + subpack, ex);
        }
    }
    
    private void deleteAssignmentToPerson(Person person) throws DaoException {
        try (Connection connection = dataSource.getConnection(); 
            PreparedStatement st = connection.prepareStatement(
                    "DELETE FROM assignedperson WHERE personid = ?")) {
                    st.setLong(1, person.getId());
                    int assignedSubpacksCount = this.getSubpacksAssignedToPerson(person).size();
                    int deletedRows = st.executeUpdate();
                    if (assignedSubpacksCount != deletedRows) {
                        throw new ServiceFailureException("Error when deleting assignment person "
                            + "to subpacks. "+assignedSubpacksCount+" should delete but was " + deletedRows);
                    }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when deleting assignment subpacks to perrson " + person, ex);
        }
    }

    @Override
    public void assignPersonToSubpack(Person person, Subpack subpack) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("Person is null");
        }
        if (person.getId() == null || person.getId() < 1) {
            throw new ValidationException("Person id is null or negative");
        }
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 1) {
            throw new ValidationException("Subpack id is null or negative");
        }
        personDao.getById(person.getId());
        this.getById(subpack.getId());
        try (Connection connection = dataSource.getConnection(); 
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO assignedperson (subpackid, personid, starttime) VALUES (?,?,?)")) {
                st.setLong(1, subpack.getId());
                st.setLong(2, person.getId());
                Calendar calendar = Calendar.getInstance();
                st.setTimestamp(3, new java.sql.Timestamp(calendar.getTime().getTime()));
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows ("
                    + addedRows + ") inserted when trying insert assignment person "
                            + person +" to subpack " + subpack);
                }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when insert assignment person "
                            + person +" to subpack " + subpack, ex);
        }
    }
    
    @Override
    public long getAssignationTime(Subpack subpack, Person person) throws DaoException {
        checkDataSource();
        if (person == null) {
            throw new IllegalArgumentException("Person is null");
        }
        if (person.getId() == null || person.getId() < 1) {
            throw new ValidationException("Person id is null or negative");
        }
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 1) {
            throw new ValidationException("Subpack id is null or negative");
        }
        if (!doesExist(subpack)) {
            throw new BeanNotExistsException("Subpack with id " + subpack.getId()+" is not in DB!");
        }
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("Person with id " + person.getId()+" is not in DB!");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT starttime FROM assignedperson WHERE subpackid = ? AND personid = ?")) {
            
            st.setLong(1, subpack.getId());
            st.setLong(2, person.getId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                    Timestamp startTime = rs.getTimestamp(1);
                    if (rs.next()) {
                        throw new ServiceFailureException(
                            "Internal error: More entities with the same subpackid and personid found " +
                            "in assignment subpacks and people");
                    }
                    return startTime.getTime();
                } else {
                    String msg = "Assignation person "+person+" to subpack " + subpack + " not found!";
                    throw new BeanNotExistsException(msg);
                }
            
        } catch (SQLException ex) {
            String msg = "Error when retriving start time for assignation person " +
            person + " to subpack " + subpack;
            throw new ServiceFailureException(msg, ex);
        }
        
    }
    
    @Override
    public List<Subpack> getSubpacksInPack(Pack pack) throws DaoException {
        checkDataSource();
        if (pack == null) {
            throw new IllegalArgumentException("Pack is null");
        }
        if (pack.getId() == null)
            throw new ValidationException("Pack id is null");
        if (packDao.getById(pack.getId()) == null) {
            throw new BeanNotExistsException(
                "Error when retriving subpacks with parent pack with id " + 
                pack.getId()+". Pack is not in DB!");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, packid, name FROM subpack WHERE packid = ?")) {
            
            st.setLong(1, pack.getId());
            ResultSet rs = st.executeQuery();
            List<Subpack> subpacks = new ArrayList<>();
            while (rs.next()) {
                subpacks.add(resultSetToSubpack(rs));
            }
            
            return subpacks;
            
        } catch (SQLException ex) {
            String msg = "Error when retriving subpacks with parent pack with id "+pack.getId();
            throw new ServiceFailureException(msg, ex);
        }
    }
}
