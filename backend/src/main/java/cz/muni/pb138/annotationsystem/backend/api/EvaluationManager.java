package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface EvaluationManager {

    void eval(Evaluation evaluation);

    Evaluation getEvaluationById(Long id) throws DaoException;

    void correct(Evaluation evaluation);

}
