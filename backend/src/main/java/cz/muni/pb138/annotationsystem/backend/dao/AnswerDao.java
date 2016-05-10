package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.EntityNotFoundException;
import cz.muni.pb138.annotationsystem.backend.common.IllegalEntityException;
import cz.muni.pb138.annotationsystem.backend.common.ServiceFailureException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import javax.sql.DataSource;
//import javax.inject.Named;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
//@Named
public class AnswerDao implements Dao<Answer> {
    
    private static final Logger logger = Logger.getLogger(
            AnswerDao.class.getName());
    
    private DataSource dataSource;
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is null");
        }
    }

    public AnswerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void validate(Answer answer) throws IllegalArgumentException {
        if (answer == null) {
            throw new IllegalArgumentException("Answer is null");
        }
        if (answer.getFromSubpack() == null) {
            throw new ValidationException("FromSubpack is null");
        }
        if (answer.getAnswer() == null) {
            throw new ValidationException("Answer-value is null");
        }
        if (answer.getAnswer().isEmpty()) {
            throw new ValidationException("Answer-value is empty");
        }
        if (answer.isIsNoise() == null) {
            throw new ValidationException("IsNoise is null");
        }
    }

    private Long getKey(ResultSet keyRS, Answer answer) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert answer " + answer
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert answer " + answer
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert answer " + answer
                    + " - no key found");
        }
    }
    
    @Override
    public void create(Answer answer) {
        validate(answer);
        checkDataSource();
        if (answer.getId() != null) {
            throw new IllegalEntityException("Answer id is already set");
        }
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO answer (subpackid, answervalue, isnoise) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS); 

            st.setLong(1, answer.getFromSubpack().getId());
            st.setString(2, answer.getAnswer());
            st.setBoolean(3, answer.isIsNoise());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert answer " + answer);
            }

            ResultSet keyRS = st.getGeneratedKeys();
            answer.setId(getKey(keyRS, answer));

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting answer " + answer, ex);
        }
    }
    
    private Answer resultSetToAnswer(ResultSet rs) throws SQLException, DaoException {
        Answer answer = new Answer();
        answer.setId(rs.getLong("id"));
        Subpack subpack = new Subpack();
        SubpackDao subpackDao = new SubpackDao(dataSource);
        subpack = subpackDao.getById(rs.getLong("subpackid"));
        answer.setFromSubpack(subpack);
        answer.setAnswer(rs.getString("answervalue"));
        answer.setIsNoise(rs.getBoolean("isnoise"));
        return answer;
    }

    @Override
    public Answer getById(Long id) throws DaoException {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, subpackid, answervalue, isnoise FROM answer WHERE id = ?"); 

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Answer answer = resultSetToAnswer(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                        "Internal error: More entities with the same id found " +
                        "(source id: " + id + ", found " + answer + " and " + 
                        resultSetToAnswer(rs));
                }

                return answer;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving subpack with id " + id, ex);
        }
    }

    @Override
    public void update(Answer answer) {
        validate(answer);
        if (answer.getId() == null) {
            throw new IllegalEntityException("Answer id is null");
        }
        try {
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE answer SET subpackid = ?, answervalue = ?, isnoise = ? WHERE id = ?");

            st.setLong(1, answer.getFromSubpack().getId());
            st.setString(2, answer.getAnswer());
            st.setBoolean(3, answer.isIsNoise());
            st.setLong(4, answer.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new IllegalEntityException("Answer " + answer + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating answer " + answer, ex);
        }
    }

    @Override
    public void delete(Answer answer) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer is null");
        }
        if (answer.getId() == null) {
            throw new IllegalArgumentException("Answer id is null");
        }
        try {
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM answer WHERE id = ?");

            st.setLong(1, answer.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Answer " + answer + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException(
                    "Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating answer " + answer, ex);
        }
    }

}
