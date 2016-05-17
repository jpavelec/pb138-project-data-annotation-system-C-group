package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import javax.inject.Named;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface AnswerManager {

    Answer nextAnswer(Person person, Subpack subpack) throws DaoException;

    Answer getAnswerById(Long id) throws DaoException;

    List<Answer> getAnswersInSubpack(Subpack subpack) throws DaoException;

    List<Answer> getAnswersInPack(Pack pack) throws DaoException;

}
