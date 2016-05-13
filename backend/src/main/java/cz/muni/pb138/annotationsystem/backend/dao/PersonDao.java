package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.common.ServiceFailureException;
import cz.muni.pb138.annotationsystem.backend.common.IllegalEntityException;
import cz.muni.pb138.annotationsystem.backend.common.EntityNotFoundException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * This class implements Dao service for Person class
 *
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@Named
public class PersonDao implements Dao<Person> {

    private static final Logger logger = Logger.getLogger(PersonDao.class.getName());
    private DataSource dataSource;

    @Inject
    public PersonDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
    private void validate(Person bean) {
        if (bean == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (bean.getUsername() == null) {
            throw new ValidationException("Username of user is null");
        }
        if (bean.getUsername().isEmpty()) {
            throw new ValidationException("Username of user is empty");
        }
    }
    
    private Long getKey(ResultSet keyRS, Person person) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert user " + person
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert user " + person
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert user " + person
                    + " - no key found");
        }
    }
    
    public static Person resultSetToPerson(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setUsername(rs.getString("username"));
        person.setIsAdmin(rs.getBoolean("isadmin"));
        return person;
    }

    @Override
    public void create(Person person) throws DaoException {
        checkDataSource();
        validate(person);
        if (person.getId() != null) {
            throw new ValidationException("User id is already set");
        }

        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO person (username, isadmin) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            st.setString(1, person.getUsername());
            st.setBoolean(2, person.getIsAdmin());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert user " + person);
            }
            
            try (ResultSet keyRS = st.getGeneratedKeys()) {
                person.setId(getKey(keyRS, person));
            }    
            
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting user " + person, ex);
        }
    }

    @Override
    public void update(Person person) throws DaoException {
        checkDataSource();
        validate(person);
        if (person.getId() == null) {
            throw new IllegalEntityException("User id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "UPDATE person SET username = ?, isadmin = ? WHERE id = ?")) {
            
            st.setString(1, person.getUsername());
            st.setBoolean(2, person.getIsAdmin());
            st.setLong(3, person.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("User " + person + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating user " + person, ex);
        }
    }

    @Override
    public void delete(Person person) throws DaoException {
        checkDataSource();
        if (person == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (person.getId() == null) {
            throw new IllegalEntityException("User id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "DELETE FROM person WHERE id = ?")) {
            
            st.setLong(1, person.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("User " + person + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating user " + person, ex);
        }
    }
    
    @Override
    public Person getById(Long id) throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id, username, isadmin FROM person WHERE id = ?")) {
            
            st.setLong(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
            
                if (rs.next()) {
                    Person person = resultSetToPerson(rs);
                
                    if (rs.next()) {
                        throw new ServiceFailureException(
                            "Internal error: More entities with the same id found " +
                            "(source id: " + id + ", found " + person + 
                            " and " + resultSetToPerson(rs));
                    }
                            
                    return person;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when retriving user with id " + id, ex);
        }
    }

    @Override
    public List<Person> getAll() throws DaoException {
        return null;
    }

}
