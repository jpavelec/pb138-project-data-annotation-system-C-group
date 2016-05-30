package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.EvaluationManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class AnswerManagerImpl implements AnswerManager {

    @Inject
    private AnswerDao answerDao;

    @Inject
    private EvaluationDao evaluationDao;

    @Override
    public Answer nextAnswer(Person person, Subpack subpack) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("person is null");
        }
        if (subpack == null) {
            throw new IllegalArgumentException("subpack is null");
        }
        if (!subpack.getUsers().contains(person)) {
            throw new IllegalArgumentException("person is not assigned to subpack");
        }

        // TODO definitely create some Dao method. e.g. getUnevaluatedAnswers(person, subpack)
        List<Evaluation> evaluations = evaluationDao.getAll();
        for (Answer a : answerDao.getAll()) {
            for (Evaluation e : evaluations) {
                if (!a.equals(e.getAnswer())) {
                    if (e.getPerson().equals(person) && e.getAnswer().getFromSubpack().equals(subpack)) {
                        return a;
                    }
                }
            }
        }
        throw new IllegalStateException("No more answers left.");
    }

    @Override
    public Answer getAnswerById(Long id) throws DaoException {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id is null or negative");
        }

        return answerDao.getById(id);
    }

    @Override
    public List<Answer> getAnswersInSubpack(Subpack subpack) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("subpack is null");
        }

        List<Answer> answers = new ArrayList<>();
        for (Answer a : answerDao.getAll()) {
            if (a.getFromSubpack().equals(subpack)) {
                answers.add(a);
            }
        }
        return answers;
    }

    @Override
    public List<Answer> getAnswersInPack(Pack pack) throws DaoException {
        if (pack == null) {
            throw new IllegalArgumentException("pack is null");
        }

        List<Answer> answers = new ArrayList<>();
        for (Answer a : answerDao.getAll()) {
            if (a.getFromSubpack().getParent().equals(pack)) {
                answers.add(a);
            }
        }
        return answers;
    }
}
