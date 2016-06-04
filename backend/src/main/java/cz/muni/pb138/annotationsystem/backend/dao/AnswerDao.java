package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface AnswerDao extends Dao<Answer> {
    
    public void createRepeatingAnswer(Answer answer) throws DaoException;

}
