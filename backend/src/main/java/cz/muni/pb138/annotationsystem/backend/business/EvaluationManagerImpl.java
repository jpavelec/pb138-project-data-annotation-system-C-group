package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.EvaluationManager;
import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDao;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDaoImpl;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class EvaluationManagerImpl implements EvaluationManager {

    @Inject
    private EvaluationDao evaluationDao;

    @Inject
    private PersonManager personManager;

    @Inject
    private AnswerManager answerManager;

    @Override
    public void eval(Evaluation evaluation) {

    }

    @Override
    public Evaluation getEvaluationById(Long id) throws DaoException {
        Person p = personManager.getPersonById((long) 1);
        Answer a = answerManager.getAnswerById((long) 1);
        return new Evaluation(p, a, Rating.POSITIVE, 42);
    }

    @Override
    public List<Evaluation> getEvaluationsOfPerson(Person person) throws DaoException {
        return new ArrayList<>();
    }
}
