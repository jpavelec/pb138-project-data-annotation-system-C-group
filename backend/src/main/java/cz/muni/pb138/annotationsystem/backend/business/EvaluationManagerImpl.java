package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.EvaluationManager;
import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDao;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDaoImpl;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

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


    @Override
    public void eval(Evaluation evaluation) throws DaoException {
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation is null");
        }
        if (evaluation.getId() != null) {
            throw new ValidationException("Created evaluation already has id");
        }
        if (evaluation.getRating() == null) {
            throw new ValidationException("Created evaluation rating is null");
        }
        if (evaluation.getPerson() == null || evaluation.getPerson().getId() == null ||  evaluation.getPerson().getId() < 0) {
            throw new ValidationException("Created evaluation person is null or has null or negative id");
        }
        if (evaluation.getAnswer() == null || evaluation.getAnswer().getId() == null ||  evaluation.getAnswer().getId() < 0) {
            throw new ValidationException("Created evaluation answer is null or has null or negative id");
        }
        if (evaluation.getElapsedTime() > 0) {
            throw new ValidationException("Evaluation time is negative");
        }

        evaluationDao.create(evaluation);
    }

    @Override
    public Evaluation getEvaluationById(Long id) throws DaoException {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id is null or negative");
        }

        return evaluationDao.getById(id);
    }

    @Override
    public List<Evaluation> getEvaluationsOfPerson(Person person) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("person is null");
        }

        List<Evaluation> evals = new ArrayList<>();
        for (Evaluation e : evaluationDao.getAll()) {
            if (e.getPerson().equals(person)) {
                evals.add(e);
            }
        }
        return evals;
    }
}
