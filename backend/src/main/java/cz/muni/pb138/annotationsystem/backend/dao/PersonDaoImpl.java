package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanAlreadyExistsException;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;import cz.muni.pb138.annotationsystem.backend.common.DaoException;

import javax.inject.Named;
import javax.sql.DataSource;

/**
 * This class implements Dao service for Person class
 *
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@Named
public class PersonDaoImpl implements PersonDao {

    @Inject
    private DataSource dataSource;

    public PersonDaoImpl() {
    }

    
    public PersonDaoImpl(DataSource dataSource) {
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
        if (bean.getUsername().length() > 64) {
            throw new ValidationException("Username of user is too long ("+
                    bean.getUsername().length()+"characters and 64 characters is maximum)");
        }
        
    }
    
    private void checkIfUsernameDoesntExist(String username) throws DaoException {
        if (this.getByUsername(username) != null) {
            throw new BeanAlreadyExistsException("User with username  " + 
                    username+" already exists");
        }
    }
    
    private Long getKey(ResultSet keyRS, Person person) throws DaoException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new DaoException("Internal Error: Generated key"
                        + "retriving failed when trying to insert user " + person
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new DaoException("Internal Error: Generated key"
                        + "retriving failed when trying to insert user " + person
                        + " - more keys found");
            }
            return result;
        } else {
            throw new DaoException("Internal Error: Generated key"
                    + "retriving failed when trying to insert user " + person
                    + " - no key found");
        }
    }
    
    public static Person resultSetToPerson(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setUsername(rs.getString("username"));
        return person;
    }

    @Override
    public boolean doesExist(Person person) throws DaoException {
        checkDataSource();
        if (person == null) {
            throw new IllegalArgumentException("Person is null");
        }
        if (person.getId() == null) {
            throw new ValidationException("Person id is null");
        }
        if (person.getId() < 0) {
            throw new ValidationException("Person id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id, username FROM person WHERE id = ?")) {
            
            st.setLong(1, person.getId());
            
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
                "Error when testing if person " + person + " is in DB", ex);
        }
    }
    
    @Override
    public void create(Person person) throws DaoException {
        checkDataSource();
        validate(person);
        if (person.getId() != null) {
            throw new ValidationException("User id is already set");
        }
        checkIfUsernameDoesntExist(person.getUsername());
        
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO person (username) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            st.setString(1, person.getUsername());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new DaoException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert user " + person);
            }
            
            try (ResultSet keyRS = st.getGeneratedKeys()) {
                person.setId(getKey(keyRS, person));
            }    
            
        } catch (SQLException ex) {
            throw new DaoException("Error when inserting user " + person, ex);
        }
    }

    @Override
    public void update(Person person) throws DaoException {
        checkDataSource();
        validate(person);
        if (person.getId() == null) {
            throw new ValidationException("User id is null");
        }
        if (person.getId() < 0) {
            throw new ValidationException("User id is negative");
        }
        checkIfUsernameDoesntExist(person.getUsername());
        
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "UPDATE person SET username = ? WHERE id = ?")) {
            
            st.setString(1, person.getUsername());
            st.setLong(2, person.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("User " + person + " was not found in database!");
            } else if (count != 1) {
                throw new DaoException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new DaoException(
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
            throw new ValidationException("User id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "DELETE FROM person WHERE id = ?")) {
            
            st.setLong(1, person.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("User " + person + " was not found in database!");
            } else if (count != 1) {
                throw new DaoException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new DaoException(
                    "Error when updating user " + person, ex);
        }
    }
    
    @Override
    public Person getById(Long id) throws DaoException {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("Id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id, username FROM person WHERE id = ?")) {
            
            st.setLong(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
            
                if (rs.next()) {
                    Person person = resultSetToPerson(rs);
                
                    if (rs.next()) {
                        throw new DaoException(
                            "Internal error: More entities with the same id found " +
                            "(source id: " + id + ", found " + person + 
                            " and " + resultSetToPerson(rs));
                    }
                            
                    return person;
                } else {
                    throw new BeanNotExistsException("Person with id " +
                            id + "isn't in DB");
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(
                "Error when retriving user with id " + id, ex);
        }
    }

    @Override
    public Person getByUsername(String username) throws DaoException {
        checkDataSource();
        if (username == null) {
            throw new IllegalArgumentException("Username is null");
        }
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username is empty");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id, username FROM person WHERE username = ?")) {
            
            st.setString(1, username);
            
            try (ResultSet rs = st.executeQuery()) {
            
                if (rs.next()) {
                    Person person = resultSetToPerson(rs);
                
                    if (rs.next()) {
                        throw new DaoException(
                            "Internal error: More entities with the same username found " +
                            "(source username: " + username + ", found " + person + 
                            " and " + resultSetToPerson(rs));
                    }
                            
                    return person;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(
                "Error when retriving user with username " + username, ex);
        }
    }
    
    

    @Override
    public List<Person> getAll() throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, username FROM person");
            ResultSet rs = st.executeQuery()) {
            List<Person> users = new ArrayList<>();
            while (rs.next()) {
                users.add(resultSetToPerson(rs));
            }
            
            return users;
            
        } catch (SQLException ex) {
            String msg = "Error when getting all users from DB";
            throw new DaoException(msg, ex);
        }
    }

}
