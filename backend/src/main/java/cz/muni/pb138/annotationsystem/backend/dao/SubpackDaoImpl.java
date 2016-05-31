package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ServiceFailureException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
            throw new ValidationException("Subpack parent is null");
        }
        checkIfPackExists(subpack.getParent());
        if (subpack.getName() == null) {
            throw new ValidationException("Subpack name is null");
        }
        if (subpack.getName().isEmpty()) {
            throw new ValidationException("Subpack name is empty");
        }
        if (subpack.getName().length() > 30) {
            throw new ValidationException("Subpack name is too long (" +
                    subpack.getName().length() + " chars but 30 is maximum");
        }
        if (subpack.getUsers() == null) {
            throw new ValidationException("List of users for subpack is null");
        }
        checkIfUsersExist(subpack.getUsers());
        
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
    
    /* TO DO switch better solution
    private void checkIfPackExists(Pack pack) throws BeanNotExistsException, DaoException {
        packDao.getById(pack.getId());
    }*/
    
    private void checkIfUsersExist(List<Person> users) throws DaoException {
        try {
            for (Person p : users) {
                personDao.getById(p.getId());
            }
        } catch(BeanNotExistsException ex) {
            String msg = "Error when creating/updating subpack - some person is not exist";
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
        try (Connection connection = dataSource.getConnection()) {
            for (Person person : subpack.getUsers()) {
                try (PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO assignedperson (subpackid, personid) VALUES (?,?)")) {
                    st.setLong(1, subpack.getId());
                    st.setLong(2, person.getId());
                    int addedRows = st.executeUpdate();
                    if (addedRows != 1) {
                        throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert subpack " + subpack);
                    }
                }
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
        subpack.setUsers(getUsersAssignedToSubpackBySubpackId(subpack.getId()));
        return subpack;
    }
    @Override
    public Subpack getById(Long id) throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, packid, name FROM subpack WHERE id = ?")) {
            
            st.setLong(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
            
                if (rs.next()) {
                    Subpack subpack = resultSetToSubpack(rs);

                    if (rs.next()) {
                        throw new ServiceFailureException(
                            "Internal error: More entities with the same id found " +
                            "(source id: " + id + ", found " + subpack + 
                            " and " + resultSetToSubpack(rs));
                    }
                    //subpack.setUsers(getUsersAssignedToSubpackBySubpackId(id));
                    return subpack;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when retriving subpack with id " + id, ex);
        }
    }
    
    private List<Person> getUsersAssignedToSubpackBySubpackId(Long id) {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT person.id, person.username FROM person INNER JOIN "
                        + "assignedperson ON person.id=assignedperson.personid"
                        + " WHERE assignedperson.subpackid = ?")) {
            
            st.setLong(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
            List<Person> assignedUsers = new ArrayList<>();
                while (rs.next()) {
                    assignedUsers.add(PersonDaoImpl.resultSetToPerson(rs));
                }
                return assignedUsers;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when retriving users assigned to subpack with id " + id, ex);
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
        if (subpack.getId() == null) {
            throw new IllegalArgumentException("Subpack id is null");
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
            //Logger.getLogger(SubpackDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServiceFailureException(
                    "Error when updating subpack " + subpack, ex);
        }
        
    }

    @Override
    public List<Person> getPersonsAssignedToSubpack(Subpack subpack) throws DaoException {
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

        
}
