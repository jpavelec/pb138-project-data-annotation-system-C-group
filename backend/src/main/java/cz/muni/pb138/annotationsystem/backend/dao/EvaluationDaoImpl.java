package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@Named
public class EvaluationDaoImpl implements EvaluationDao {

    @Inject
    private DataSource dataSource;

    @Inject
    private SubpackDaoImpl subpackDao;
    
    @Inject
    private AnswerDaoImpl answerDao;
    
    @Inject
    private PersonDaoImpl personDao;

    public EvaluationDaoImpl() {
    }
    
    public EvaluationDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
    
    private void validate(Evaluation evaluation) {
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation is null");
        }
        if (evaluation.getPerson() == null) {
            throw new ValidationException("User of evaluation is null");
        }
        if (evaluation.getAnswer() == null) {
            throw new ValidationException("Answer of evaluation is null");
        }
        if (evaluation.getRating() == null) {
            throw new ValidationException("Rating of evaluation is null");
        }
    }
    
    private Long getKey(ResultSet keyRS, Evaluation evaluation) throws DaoException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new DaoException("Internal Error: Generated key"
                        + "retriving failed when trying to insert evaluation " + evaluation
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new DaoException("Internal Error: Generated key"
                        + "retriving failed when trying to insert evaluation " + evaluation
                        + " - more keys found");
            }
            return result;
        } else {
            throw new DaoException("Internal Error: Generated key"
                    + "retriving failed when trying to insert evaluation " + evaluation
                    + " - no key found");
        }
    }
    
    public Evaluation resultSetToEvaluation(ResultSet rs) throws SQLException, DaoException {
        Evaluation eval = new Evaluation();
        eval.setId(rs.getLong("id"));
        PersonDaoImpl personDao = new PersonDaoImpl(dataSource);
        Person person = new Person();
        person = personDao.getById(rs.getLong("personid"));
        AnswerDao answerDao = new AnswerDaoImpl(dataSource);
        Answer answer = new Answer();
        answer = answerDao.getById(rs.getLong("answerid"));
        eval.setPerson(person);
        eval.setAnswer(answer);
        eval.setRating(Rating.valueOf(rs.getString("rating")));
        eval.setElapsedTime(rs.getInt("elapsedtime"));
        return eval;
    }
    
    @Override
    public boolean doesExist(Evaluation evaluation) throws DaoException {
        checkDataSource();
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation is null");
        }
        if (evaluation.getId() == null) {
            throw new ValidationException("Evaluation id is null");
        }
        if (evaluation.getId() < 0) {
            throw new ValidationException("Evaluation id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id FROM evaluation WHERE id = ?")) {
            
            st.setLong(1, evaluation.getId());
            
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
                "Error when testing if evaluation " + evaluation + " is in DB", ex);
        }
    }
    
    @Override
    public void create(Evaluation evaluation) throws DaoException {
        checkDataSource();
        validate(evaluation);
        if (evaluation.getId() != null) {
            throw new ValidationException("Evaluation id is already set");
        }
        
        /*if (answerDao.isInsertingLastEvaluation(evaluation.getAnswer().
                    getFromSubpack(), evaluation.getPerson())) {
            subpackDao.setCompletationTime(evaluation.getAnswer().
                    getFromSubpack(), evaluation.getPerson());
        }*/
        
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO evaluation (personid, answerid, rating, elapsedTime) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            st.setLong(1, evaluation.getPerson().getId());
            st.setLong(2, evaluation.getAnswer().getId());
            st.setString(3, evaluation.getRating().toString());
            st.setInt(4, evaluation.getElapsedTime());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new DaoException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert evaluation " + evaluation);
            }
            
            try (ResultSet keyRS = st.getGeneratedKeys()) {
                evaluation.setId(getKey(keyRS, evaluation));
            }
            
            if (answerDao.isSubpackCompletelyEvaluated(evaluation.getAnswer().
                    getFromSubpack(), evaluation.getPerson())) {
                setCompletationTime(evaluation.getAnswer().
                    getFromSubpack(), evaluation.getPerson());
        }
            
        } catch (SQLException ex) {
            throw new DaoException("Error when inserting evaluation " + evaluation, ex);
        }
    }
    
    private void setCompletationTime(Subpack subpack, Person person) throws DaoException {
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
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("Subpack with id " + subpack.getId()+" is not in DB!");
        }
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("Person with id " + person.getId()+" is not in DB!");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "UPDATE assignedperson SET endtime = ? " + 
                "WHERE subpackid = ? AND personid = ?")) {
            
            Calendar calendar = Calendar.getInstance();
            st.setTimestamp(1, new java.sql.Timestamp(calendar.getTime().getTime()));
            st.setLong(2, subpack.getId());
            st.setLong(3, person.getId());
            System.err.println("Time: "+new java.sql.Timestamp(calendar.getTime().getTime())+" "+person+subpack);
            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("Invalid records in assignedperson");
            } else if (count != 1) {
                throw new DaoException(
                "Invalid updated rows count detected (one row should be updated): " + count);
            }
            
        } catch (SQLException ex) {
            String msg = "Error when inserting completation time for assignation person " +
            person + " to subpack " + subpack;
            throw new DaoException(msg, ex);
        }
    }

    @Override
    public Evaluation getById(Long id) throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, personid, answerid, rating, elapsedTime FROM evaluation WHERE id = ?")) {
            
            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
            
                if (rs.next()) {
                    Evaluation eval = resultSetToEvaluation(rs);

                    if (rs.next()) {
                        throw new DaoException(
                            "Internal error: More entities with the same id found " +
                            "(source id: " + id + ", found " + eval + 
                            " and " + resultSetToEvaluation(rs));
                    }
                    return eval;
                } else {
                    throw new BeanNotExistsException("Evaluation with id "+id+" is not in DB");
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(
                "Error when retriving evaluation with id " + id, ex);
        }
    }

    @Override
    public List<Evaluation> getAll() throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, personid, answerid, rating, elapsedtime FROM evaluation");
            ResultSet rs = st.executeQuery()) {
            List<Evaluation> evaluations = new ArrayList<>();
            while (rs.next()) {
                evaluations.add(resultSetToEvaluation(rs));
            }
            
            return evaluations;
            
        } catch (SQLException ex) {
            String msg = "Error when getting all evaluations from DB";
            throw new DaoException(msg, ex);
        }
    }

    @Override
    public void update(Evaluation evaluation) throws DaoException {
        checkDataSource();
        validate(evaluation);
        if (evaluation.getId() == null) {
            throw new ValidationException("Evaluation id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "UPDATE evaluation SET personid = ?, answerid = ?, " +
                    "rating = ?, elapsedTime = ? WHERE id = ?")) {
            
            st.setLong(1, evaluation.getPerson().getId());
            st.setLong(2, evaluation.getAnswer().getId());
            st.setString(3, evaluation.getRating().toString());
            st.setInt(4, evaluation.getElapsedTime());
            st.setLong(5, evaluation.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("Evaluation " + evaluation + " was not found in database!");
            } else if (count != 1) {
                throw new DaoException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new DaoException(
                "Error when updating evaluation " + evaluation, ex);
        }
    }

    @Override
    public void delete(Evaluation evaluation) throws DaoException {
        checkDataSource();
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation is null");
        }
        if (evaluation.getId() == null) {
            throw new ValidationException("Evaluation id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "DELETE FROM evaluation WHERE id = ?")) {
            
            st.setLong(1, evaluation.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("Evaluation " + evaluation + " was not found in database!");
            } else if (count != 1) {
                throw new DaoException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new DaoException(
                    "Error when deleting evaluation " + evaluation, ex);
        }
    }
}
