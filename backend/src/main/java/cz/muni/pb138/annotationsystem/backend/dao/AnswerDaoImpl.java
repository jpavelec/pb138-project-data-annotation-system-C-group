package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ServiceFailureException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@Named
public class AnswerDaoImpl implements AnswerDao {

    @Inject
    private DataSource dataSource;

    @Inject
    private EvaluationDao evalDao;
    
    @Inject
    private SubpackDaoImpl subpackDao;
    
    @Inject
    private PersonDaoImpl personDao;
    
    public AnswerDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public AnswerDaoImpl(){}
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is null");
        }
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
    public boolean doesExist(Answer answer) throws DaoException {
        checkDataSource();
        if (answer == null) {
            throw new IllegalArgumentException("Answer is null");
        }
        if (answer.getId() == null) {
            throw new ValidationException("Answer id is null");
        }
        if (answer.getId() < 0) {
            throw new ValidationException("Answer id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id FROM answer WHERE id = ?")) {
            
            st.setLong(1, answer.getId());
            
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
                "Error when testing if answer " + answer + " is in DB", ex);
        }
    }
    
    @Override
    public void create(Answer answer) {
        validate(answer);
        checkDataSource();
        if (answer.getId() != null) {
            throw new ValidationException("Answer id is already set");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO answer (subpackid, answervalue, isnoise) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

            st.setLong(1, answer.getFromSubpack().getId());
            st.setString(2, answer.getAnswer());
            st.setBoolean(3, answer.isIsNoise());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert answer " + answer);
            }

            try (ResultSet keyRS = st.getGeneratedKeys()) {
                answer.setId(getKey(keyRS, answer));
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting answer " + answer, ex);
        }
    }

    public Answer resultSetToAnswer(ResultSet rs) throws SQLException, DaoException {
        Answer answer = new Answer();
        answer.setId(rs.getLong(1)); // id
        Subpack subpack = new Subpack();
        SubpackDaoImpl subpackDao = new SubpackDaoImpl(dataSource);
        subpack = subpackDao.getById(rs.getLong(2)); // subpackid
        answer.setFromSubpack(subpack);
        answer.setAnswer(rs.getString(3)); // answervalue
        answer.setIsNoise(rs.getBoolean(4)); // isnoise
        return answer;
    }

    @Override
    public Answer getById(Long id) throws DaoException {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, subpackid, answervalue, isnoise FROM answer WHERE id = ?")) {

            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    Answer answer = resultSetToAnswer(rs);

                    if (rs.next()) {
                        throw new ServiceFailureException(
                                "Internal error: More entities with the same id found "
                                + "(source id: " + id + ", found " + answer + " and "
                                + resultSetToAnswer(rs));
                    }

                    return answer;
                } else {
                    throw new BeanNotExistsException("Answer with id "+id+" is not in DB");
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving subpack with id " + id, ex);
        }
    }

    @Override
    public List<Answer> getAll() throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, subpackid, answervalue, isnoise FROM answer");
            ResultSet rs = st.executeQuery()) {
            List<Answer> answers = new ArrayList<>();
            while (rs.next()) {
                answers.add(resultSetToAnswer(rs));
            }
            
            return answers;
            
        } catch (SQLException ex) {
            String msg = "Error when getting all answers from DB";
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void update(Answer answer) {
        checkDataSource();
        validate(answer);
        if (answer.getId() == null) {
            throw new ValidationException("Answer id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "UPDATE answer SET subpackid = ?, answervalue = ?, isnoise = ? WHERE id = ?")) {

            st.setLong(1, answer.getFromSubpack().getId());
            st.setString(2, answer.getAnswer());
            st.setBoolean(3, answer.isIsNoise());
            st.setLong(4, answer.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                String msg = "Error when updating answer - answer with id " +
                        answer.getId() +" was not found in DB";
                throw new BeanNotExistsException(msg);
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating answer " + answer, ex);
        }
    }

    @Override
    public void delete(Answer answer) throws DaoException {
        checkDataSource();
        if (answer == null) {
            throw new IllegalArgumentException("Answer is null");
        }
        if (answer.getId() == null) {
            throw new ValidationException("Answer id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "DELETE FROM answer WHERE id = ?")) {

            st.setLong(1, answer.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("Answer " + answer + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException(
                        "Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when deleting answer " + answer, ex);
        }
    }

    @Override
    public void createRepeatingAnswer(Answer answer) throws DaoException {
        if (answer == null) {
            throw new IllegalArgumentException("Answer is null");
        }
        if (answer.getId() == null || answer.getId() < 0) {
            throw new ValidationException("Answer id is null");
        }
        if (!doesExist(answer)) {
            throw new BeanNotExistsException("Answer with id "+answer.getId()+" is not in DB!");
        }
        if (answer.getFromSubpack() == null) {
            throw new ValidationException("Answer fromSubpack is null");
        }
        if (!subpackDao.doesExist(answer.getFromSubpack())) {
            throw new BeanNotExistsException("Subpack "+answer.getFromSubpack()+" is not in DB!");
        }
        if (answer.isIsNoise() == null) {
            throw new ValidationException("Answer isNoise is null");
        }
        if (answer.getAnswer() == null || answer.getAnswer().isEmpty()) {
            throw new ValidationException("Answer answer is null or empty");
        }
        checkDataSource();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement st = conn.prepareStatement(
             "INSERT INTO REPEATANSWER values (?,?)")) {
            
            st.setLong(1, answer.getFromSubpack().getId());
            st.setLong(2, answer.getId());
            
            int rows = st.executeUpdate();
            if (rows != 1) {
                throw new DaoException("DB error - more rows was inserted when inserting repeat answer");
            }
            
        } catch (SQLException ex) {
            throw new DaoException("DB error when insert repeat answer", ex);
        }
    }
    
    @Override
    public List<Answer> getAnswersInSubpack(Subpack subpack) throws DaoException {
        checkDataSource();
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("Subpack id is null or negative");
        }
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("Subpack " + subpack + " not found in DB!");
        }
        //CopyOnWriteArrayList<Answer> answersInSubpack = new CopyOnWriteArrayList<>();
        List<Answer> answersInSubpack = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement st = conn.prepareStatement(
             "SELECT answer.id, answer.subpackid, answervalue, isnoise FROM answer " +
             "WHERE answer.subpackid = ? " +
             "UNION ALL "+
             "SELECT answer.id, answer.subpackid, answervalue, isnoise FROM answer INNER JOIN " +
             "repeatanswer ON answer.id=repeatanswer.answerid WHERE answer.subpackid = ?");) {
            
            st.setLong(1, subpack.getId());
            st.setLong(2, subpack.getId());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                answersInSubpack.add(resultSetToAnswer(rs));
            }
            return answersInSubpack;
         
        } catch (SQLException ex) {
            String msg = "DB error when retriving answers from subpack " + subpack;
            throw new DaoException(msg, ex);
        } 
    }

    @Override
    public List<Answer> getUnevaluatedAnswers(Subpack subpack, Person person) throws DaoException {
        /*CopyOnWriteArrayList<Answer> unevaluatedAnswers = new CopyOnWriteArrayList<>();
        unevaluatedAnswers.addAll(getAnswersInSubpack(subpack));
        CopyOnWriteArrayList<Answer> evaluatedAnswers = new CopyOnWriteArrayList<>();
        evaluatedAnswers.addAll(getEvaluatedAnswers(subpack, person));*/
        List<Answer> unevaluatedAnswers = getAnswersInSubpack(subpack);
        List<Answer> evaluatedAnswers = getEvaluatedAnswers(subpack, person);
        Iterator<Answer> i = unevaluatedAnswers.iterator();
        while (i.hasNext()) {
            Answer a = i.next();
            if (evaluatedAnswers.contains(a)) {
                i.remove();
                unevaluatedAnswers.remove(a);
            }
        }
        return unevaluatedAnswers;
    }
    
    @Override
    public List<Answer> getEvaluatedAnswers(Subpack subpack, Person person) throws DaoException {
        checkDataSource();
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("Subpack id is null or negative");
        }
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("Subpack " + subpack + " not found in DB!");
        }
        if (person == null) {
            throw new IllegalArgumentException("Person is null");
        }
        if (person.getId() == null || person.getId() < 0) {
            throw new ValidationException("Person id is null or negative");
        }
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("Person " + person + " not found in DB!");
        }
        List<Answer> evaluatedAnswers = new ArrayList<>();
        //CopyOnWriteArrayList<Answer> evaluatedAnswers = new CopyOnWriteArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement(
            "SELECT answer.id, subpackid, answervalue, isnoise "+
            "FROM answer INNER JOIN evaluation ON answer.id = evaluation.answerid "+
            "WHERE subpackid = ? AND personid = ?");) {
            
            st.setLong(1, subpack.getId());
            st.setLong(2, person.getId());
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    evaluatedAnswers.add(resultSetToAnswer(rs));
                }
            }
            return evaluatedAnswers;
        } catch (SQLException ex) {
            throw new DaoException(
                "Error when retriving evaluated answers from subpack " + subpack +
                "for person "+person, ex);
        }
    }

    @Override
    public boolean isInsertingLastEvaluation(Subpack subpack, Person person) throws DaoException {
        if (getUnevaluatedAnswers(subpack, person).size() == 1) {
            return true;
        } else {
            return false;
        }
    }

}
