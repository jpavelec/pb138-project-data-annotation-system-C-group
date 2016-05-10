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
import java.util.logging.Logger;
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
    
    private Long getKey(ResultSet keyRS, Person bean) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert user " + bean
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert user " + bean
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert user " + bean
                    + " - no key found");
        }
    }
    
    public static Person resultSetToUser(ResultSet rs) throws SQLException {
        Person user = new Person();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setIsAdmin(rs.getBoolean("isadmin"));
        return user;
    }

    @Override
    public void create(Person bean) throws DaoException {
        validate(bean);
        if (bean.getId() != null) {
            throw new ValidationException("User id is already set");
        }

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO person (username, isadmin) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, bean.getUsername());
            st.setBoolean(2, bean.getIsAdmin());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert user " + bean);
            }
            
            ResultSet keyRS = st.getGeneratedKeys();
            bean.setId(getKey(keyRS, bean));
            
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting user " + bean, ex);
        }
    }

    @Override
    public void update(Person user) throws DaoException {
        validate(user);
        if (user.getId() == null) {
            throw new IllegalEntityException("User id is null");
        }
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareCall(
                    "UPDATE person SET username = ?, isadmin = ? WHERE id = ?");
            st.setString(1, user.getUsername());
            st.setBoolean(2, user.getIsAdmin());
            st.setLong(3, user.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("User " + user + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating user " + user, ex);
        }
    }

    @Override
    public void delete(Person user) throws DaoException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("User id is null");
        }
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "DELETE FROM person WHERE id = ?");
            
            st.setLong(1, user.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("User " + user + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating user " + user, ex);
        }
    }
    
    @Override
    public Person getById(Long id) throws DaoException {
        checkDataSource();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id, username, isadmin FROM person WHERE id = ?");
            
            st.setLong(1, id);
            
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                Person user = resultSetToUser(rs);
                
                if (rs.next()) {
                    throw new ServiceFailureException(
                        "Internal error: More entities with the same id found " +
                        "(source id: " + id + ", found " + user + 
                        " and " + resultSetToUser(rs));
                }
                return user;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when retriving user with id " + id, ex);
        }
    }

}
