package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface AnswerDao extends Dao<Answer> {
    
    public void createRepeatingAnswer(Answer answer) throws DaoException;
    
    public List<Answer> getAnswersInSubpack(Subpack subpack) throws DaoException;
    
    public List<Answer> getUnevaluatedAnswers(Subpack subpack, Person person) throws DaoException;
    
    public List<Answer> getEvaluatedAnswers(Subpack subpack, Person person) throws DaoException;
    
    public boolean isInsertingLastEvaluation(Subpack subpack, Person person) throws DaoException;

}
