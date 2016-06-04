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

    /**
     * Gets random answer unevaluated by given person in given subpack. It can be regular answer, Repeated answer or noise.
     * Depends on parameters in createPack method. Given person has to be assigned to the subpack. Otherwise exception is thrown.
     * If no more unevaluated answers left IllegalStateException is thrown.
     *
     * @param person person who did not evaluate answer
     * @param subpack
     * @return Random answer
     * @throws DaoException
     */
    Answer nextAnswer(Person person, Subpack subpack) throws DaoException;

    /**
     * @param id of requested answer
     * @return Answer with given id
     * @throws DaoException
     */
    Answer getAnswerById(Long id) throws DaoException;

    /**
     * Return all answers in given subpack. Also including Noise and repeating answers.
     *
     * @param subpack subpack that contains requested answers
     * @return list of all answers in subpack including noise and repeating answers
     * @throws DaoException
     */
    List<Answer> getAnswersInSubpack(Subpack subpack) throws DaoException;

    /**
     * Return all regular answers in given pack. It does NOT return repeating or noise answers.
     *
     * @param pack subpack that contains requested answers
     * @return list of all regular answers in pack except noise and repeating answers
     * @throws DaoException
     */
    List<Answer> getAnswersInPack(Pack pack) throws DaoException;

}
