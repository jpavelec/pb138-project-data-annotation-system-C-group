package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.util.List;

/**
 * @author Josef Pavelec, Faculty of Informatics, Masaryk University
 */
public interface AnswerDao extends Dao<Answer> {
    
    /**
     * Method create answer which should be repeat in annotation
     * @param answer answer to repeat
     * @throws DaoException when some database error occurs
     */
    public void createRepeatingAnswer(Answer answer) throws DaoException;
    
    /**
     * Method for finding all answers (including noise and repeating answers) in subpack
     * @param subpack input subpack with answers
     * @return answers from subpack as List
     * @throws DaoException when some database error occurs
     */
    public List<Answer> getAnswersInSubpack(Subpack subpack) throws DaoException;
    
    /**
     * Method for finding answers which should be evaluated by person from subpack
     * @param subpack input subpack with answers
     * @param person person who evaluates answers from subpack
     * @return unevaluated (by person) answers from subpack
     * @throws DaoException when some database error occurs
     */
    public List<Answer> getUnevaluatedAnswers(Subpack subpack, Person person) throws DaoException;
    
    /**
     * Method for finding answers which are evaluated by person from subpack
     * @param subpack input subpack with answers
     * @param person person who evaluates answers from subpack
     * @return evaluated (by person) answers from subpack
     * @throws DaoException when some database error occurs 
     */
    public List<Answer> getEvaluatedAnswers(Subpack subpack, Person person) throws DaoException;
    
    /**
     * Method to check if subpack is completely evaluated by person
     * @param subpack input subpack with answers
     * @param person person who evaluates answers from subpack
     * @return true when subpack is completely evaluated by person. False otherwise
     * @throws DaoException when some database error occurs  
     */
    public boolean isSubpackCompletelyEvaluated(Subpack subpack, Person person) throws DaoException;
}
