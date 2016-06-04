package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface EvaluationManager {

    /**
     * Create evaluation. Means it evaluate answer (inside given evaluation) by person (inside given evaluation).
     * One answer CAN BE evaluated more times by one person. Because of repeating answers.
     *
     * @param evaluation evaluation to be created
     * @throws DaoException
     */
    void eval(Evaluation evaluation) throws DaoException;

    /**
     * Correct already created evaluation. If evaluation does not exists exception is thrown.
     *
     * @param evaluation evaluation to be corrected
     * @throws DaoException
     */
    void correct(Evaluation evaluation) throws DaoException;

    /**
     * @return evaluation with given id
     * @param id Id of requested evaluation
     * @throws DaoException
     */
    Evaluation getEvaluationById(Long id) throws DaoException;

    /**
     * @param person person who evaluate the answers (create evaluation)
     * @return list of all evaliation with given person
     * @throws DaoException
     */
    List<Evaluation> getEvaluationsOfPerson(Person person) throws DaoException;
}
