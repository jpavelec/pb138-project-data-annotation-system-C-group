package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.EntityNotFoundException;
import cz.muni.pb138.annotationsystem.backend.common.IllegalEntityException;
import cz.muni.pb138.annotationsystem.backend.common.ServiceFailureException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@Named
public class EvaluationDao implements Dao<Evaluation> {

    private DataSource dataSource;

    @Inject
    public EvaluationDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
    
    private void validate(Evaluation bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Evaluation is null");
        }
        if (bean.getPerson() == null) {
            throw new ValidationException("User of evaluation is null");
        }
        if (bean.getAnswer() == null) {
            throw new ValidationException("Answer of evaluation is null");
        }
        if (bean.getRating() == null) {
            throw new ValidationException("Rating of evaluation is null");
        }
    }
    
    private Long getKey(ResultSet keyRS, Evaluation bean) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert evaluation " + bean
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert evaluation " + bean
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert evaluation " + bean
                    + " - no key found");
        }
    }
    
    public Evaluation resultSetToEvaluation(ResultSet rs) throws SQLException, DaoException {
        Evaluation eval = new Evaluation();
        eval.setId(rs.getLong("id"));
        PersonDao personDao = new PersonDao(dataSource);
        Person person = new Person();
        person = personDao.getById(rs.getLong("personid"));
        AnswerDao answerDao = new AnswerDaoImpl(dataSource);
        Answer answer = new Answer();
        answer = answerDao.getById(rs.getLong("answerid"));
        eval.setPerson(person);
        eval.setAnswer(answer);
        eval.setRating(Rating.getRating(rs.getString("rating")));
        eval.setElapsedTime(rs.getInt("elapsedtime"));
        return eval;
    }
    
    
    @Override
    public void create(Evaluation bean) throws DaoException {
        //validate(bean);
        if (bean.getId() != null) {
            throw new ValidationException("Evaluation id is already set");
        }
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO evaluation (personid, answerid, rating, elapsedTime) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, bean.getPerson().getId());
            st.setLong(2, bean.getAnswer().getId());
            st.setString(3, bean.getRating().getSymbol());
            st.setInt(4, bean.getElapsedTime());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert evaluation " + bean);
            }
            
            ResultSet keyRS = st.getGeneratedKeys();
            bean.setId(getKey(keyRS, bean));
            
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting user " + bean, ex);
        }
    }

    @Override
    public Evaluation getById(Long id) throws DaoException {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, personid, answerid, rating, elapsedTime FROM evaluation WHERE id = ?");
            
            st.setLong(1, id);

            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                Evaluation eval = resultSetToEvaluation(rs);
                
                if (rs.next()) {
                    throw new ServiceFailureException(
                        "Internal error: More entities with the same id found " +
                        "(source id: " + id + ", found " + eval + 
                        " and " + resultSetToEvaluation(rs));
                }
                return eval;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when retriving evaluation with id " + id, ex);
        }
    }

    @Override
    public List<Evaluation> getAll() throws DaoException {
        return null;
    }

    @Override
    public void update(Evaluation bean) throws DaoException {
        validate(bean);
        if (bean.getId() == null) {
            throw new IllegalEntityException("Evaluation id is null");
        }
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareCall(
                    "UPDATE evaluation SET personid = ?, answerid = ?, " +
                    "rating = ?, elapsedTime = ? WHERE id = ?");
            st.setLong(1, bean.getPerson().getId());
            st.setLong(2, bean.getAnswer().getId());
            st.setString(3, bean.getRating().getSymbol());
            st.setInt(4, bean.getElapsedTime());
            st.setLong(5, bean.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Evaluation " + bean + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating evaluation " + bean, ex);
        }
    }

    @Override
    public void delete(Evaluation bean) throws DaoException {
        if (bean == null) {
            throw new IllegalArgumentException("Evaluation is null");
        }
        if (bean.getId() == null) {
            throw new IllegalArgumentException("Evaluation id is null");
        }
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "DELETE FROM evaluation WHERE id = ?");
            
            st.setLong(1, bean.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Evaluation " + bean + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating evaluation " + bean, ex);
        }
    }

}
